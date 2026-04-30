# Smart_Campus_API


## Conceptual Report - Question Answers

### Part 1: Service Architecture & Setup

**Question 1.1 - Project & Application Configuration**

In your report, explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.

**Answer:**  
JAX-RS creates a brand-new instance of each Resource class for every incoming HTTP request by default. This is the JAX_RS default lifecycle known as per-request scope. Resource classes are not singletons.  

Impact on shared state:  
If shared data such as a rooms HashMap were stored as an instance field directly inside a resource class, it would be re-initialised to an empty map on every single request. All data created in one request would be completely lost by the next request.  

Solution - Singleton DataStore:  
To solve this, all shared in-memory collections are stored in a dedicated DataStore class implemented as a Java Singleton (private static final DataStore INSTANCE = new DataStore()). Because the JVM loads a class only once, there is exactly one DataStore instance for the entire lifetime of the server process, regardless of how many Resource instances JAX-RS creates.  

Thread Safety - ConcurrentHashMap:  
Multiple HTTP requests can arrive simultaneously on different threads. Using a plain HashMap would cause race conditions i.e. two threads writing at the same time can corrupt its internal state, causing data loss or ConcurrentModificationException crashes. ConcurrentHashMap solves this through segment-level locking:  
• Reads are non-blocking and fully concurrent across all threads  
• Writes lock only the affected map segment, not the entire data structure  
• No explicit synchronized keyword is required in resource code  
• Deadlocks are avoided while full data integrity is maintained  

This design (singleton store backed by ConcurrentHashMap) prevents race conditions, ensures data persists across all requests, and represents the correct architectural pattern for in-memory JAX-RS applications.

---

**Question 1.2 - The “Discovery” Endpoint**

Why is the provision of “Hypermedia” (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?

**Answer:** 
HATEOAS (Hypermedia As The Engine Of Application State) means that API responses include navigational links pointing to related resources and available next actions. Clients follow links returned dynamically in each response, exactly as a web browser follows hyperlinks on a webpage rather than hardcoding URLs into client applications.  

Benefits over static documentation:  
• Self-documenting at runtime: The API describes itself in every response. Static documentation goes stale over time; hypermedia links are always accurate and current.  
• Reduced coupling: Clients are not tightly bound to specific URL structures. If a path changes, clients that navigate via links continue to work without any code modifications.  
• Discoverability: A developer can start at GET /api/v1 and navigate the entire API from that single entry point, discovering all available resources organically.  
• Evolvability: New resources can be added and exposed through links without breaking existing clients, who simply ignore unknown link keys they have not implemented yet.  
• Self-service for developers: Client developers can explore the API without reading documentation first, significantly reducing onboarding time and support queries.

---

### Part 2: Room Management

**Question 2.1 - RoomResource Implementation**

When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client side processing.

**Answer:**  
Returning Id only:  
• Minimal bandwidth per list response as only identifiers are transmitted  
• Creates the N+1 problem: if there are 100 rooms, the client must fire 100 additional GET /rooms/{id} requests to display any useful information  
• Multiplies network latency, server load, and client-side processing complexity dramatically  

Returning Full object (implemented in this project):  
• One single request provides everything the client needs (id, name, capacity, sensorIds) with no follow-up calls required  
• Slightly larger individual responses, but far fewer total HTTP round-trips overall  
• Preferred for dashboards and UIs that always need complete room data  

Conclusion: A campus management dashboard always requires room names and capacities. Therefore, returning full objects is the correct and practical design choice. For very large collections with thousands of records, the industry standard is full objects combined with server-side pagination (e.g. GET /rooms?page=1&size=20), not ID-only responses.

---

**Question 2.2 - RoomDeletion & Safety Logic**

Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple time.

**Answer:**  
Yes, the DELETE method is idempotent in my implementation. Idempotency means that making the same request N times produces the same server state as making it once.  

In my implementation:  
• 1st DELETE /rooms/ENG-201 (room exists with no sensors): returns HTTP 200 OK, room is removed from the data store.  
• 2nd DELETE /rooms/ENG-201 (room already deleted): returns HTTP 404 Not Found  
• 3rd DELETE /rooms/ENG-201 (room already deleted): returns HTTP 404 Not Found  

The HTTP status code differs between the first and subsequent calls (200 vs 404), but the server state is identical after the first call: the room does not exist. RFC 7231 explicitly permits returning 404 on repeated DELETE calls and considers this behavior fully idempotent. The standard states that idempotency is about the observable effect on server state, not about producing consistent status codes.  

The practical benefit of this idempotency guarantee is that clients can safely retry a DELETE request on network failure without risking unintended side effects such as double-deletion or data corruption.

---

### Part 3: Sensor Operations & Linking

**Question 3.1 - Sensor Resource & Integrity**

We explicitly use the @Consumes (MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?

**Answer:** 
The @Consumes(MediaType.APPLICATION_JSON) annotation registers a strict contract at the JAX-RS framework level i.e. the POST endpoint will only accept requests whose Content-Type header is application/json.  

When a client sends a different media type (example: text/plain or application/xml) the JAX-RS runtime intercepts the request before the resource method is ever invoked. It inspects the incoming Content-Type header, finds no matching @Consumes declaration, and automatically returns: HTTP 415 Unsupported Media Type  
This behavior is handled entirely by the Jersey framework with zero custom code. The resource method body is never reached, protecting all business logic from unexpected data formats. This enforces a strict API contract at the boundary and prevents invalid payloads from entering the application processing pipeline.

---

**Question 3.2 - Filtered Retrieval & Search**

You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/vl/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?

**Answer:**  
The two approaches are:  
• GET /sensors?type=CO2 (Query parameter – implemented in this project)  
• GET /sensors/type/CO2 (Path segment)  

Using query parameters (e.g., /api/v1/sensors?type=CO2) is considered superior because it better represents REST semantics. The endpoint /api/v1/sensors is a collection, and filters like type=CO2 refine the collection rather than defining a new resource. In contrast, a path such as /api/v1/sensors/type/CO2 incorrectly suggests a separate, addressable resource.  

Query parameters are also naturally optional. If no filter is provided, the API can return all sensors without changing the endpoint. With path-based filtering, removing /type/CO2 changes the URL structure and may result in a 404 Not Found, reducing flexibility.  

They also support better composability. Multiple filters can be combined easily (e.g., ?type=CO2&status=ACTIVE), whereas path-based approaches become rigid and complex.  

Finally, query strings are the standard approach for filtering according to RFC 3986 URI Generic Syntax, making them more consistent with industry practices.

---

### Part 4: Deep Nesting with Sub- Resources

**Question 4.1 - The Sub-Resource Locator Pattern**

Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?

**Answer:**  
The Sub-Resource Locator pattern allows a resource method with no HTTP verb annotation to return an object instance whose methods handle further path routing. In this project, SensorResource.getReadingsResource() returns a SensorReadingResource instance, which handles GET and POST operations on the /readings path.  

1. Single Responsibility Principle: SensorResource handles sensor CRUD operations only. All reading history logic is fully isolated in SensorReadingResource. Each class has one clear, auditable purpose.  
2. Avoids God Classes: Without the locator pattern, a single controller class would grow to handle sensors, readings, and all future nested resources. At scale such classes become unmaintainable and prone to merge conflicts in team development.  
3. Testability: SensorReadingResource can be unit-tested in complete isolation with simple mocking of the DataStore, without instantiating or running SensorResource at all.  
4. Reusability and Extensibility: The reading resource class can be extended - for example adding GET /readings/{readingId} - without touching SensorResource at all.  
5. Clean Validation Chain: The locator method validates that the sensorId exists before instantiating the sub-resource, providing a clean separation between guard logic and core business logic.

---

### Part 5: Advanced Error Handling, Exception Mapping & Logging

**Question 5.1 - Sensor Resource & Integrity**

Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?

**Answer:**  
When POST /sensors is called with a roomId that does not exist in the system, HTTP 422 is more semantically correct.  

HTTP 404 Not Found (wrong):  
HTTP 404 means the request URL was not found on the server. This would be incorrect and misleading because the endpoint /api/v1/sensors is perfectly valid and was reached successfully. Using 404 would make developers think they typed the wrong URL.  

HTTP 422 Unprocessable Entity (correct):  
HTTP 422 means a field inside the payload references a resource that does not exist. The request URL was found and reached successfully. The Content-Type header is correct (application/json) and the JSON payload was parsed without any syntax errors. But the semantic content is logically invalid: a field inside the valid payload (roomId) references a resource that does not exist in the system  

422 precisely communicates that your request is structurally sound but logically impossible to fulfil i.e. a reference and dependency violation, not a missing URL. This distinction provides client developers with precise, actionable error information that helps them debug the issue quickly and correctly.

---

**Question 5.2 - The Global Safety Net (500)**

From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?

**Answer:**  
Exposing raw Java stack traces to external API consumers is a critical security vulnerability. An attacker can extract the following specific information:  
1. Internal Class Names and Package Structure: Stack traces reveal the full application architecture. For example, com.smartcampus.store.DataStore. This makes it trivial for an attacker to understand the code layout and precisely target specific components.  

2. Library Names and Exact Versions: Traces expose dependency details such as Jersey 2.41 or Jackson 2.15.2. An attacker can look up CVEs (Common Vulnerabilities and Exposures) for those exact versions and launch targeted known-vulnerability exploits.  

3. Server File System Paths: Stack traces include absolute file paths. This reveals the deployment directory structure and can enable path traversal attacks or reveal sensitive system information.  

4. Application Logic Flow and Failure Points: The trace reveals exactly which method failed, on which line, and under what conditions. This helps attackers craft specific inputs to trigger further errors or bypass validation logic.  

5. Database and Schema Details: SQL error messages embedded in stack traces expose table names, column names, and query structures which can be used directly for SQL injection attack planning.  

The GlobalExceptionMapper in this project addresses all these risks by logging the full stack trace server-side only (via java.util.logging.Logger at SEVERE level) and returning only a generic HTTP 500 message to the client - never exposing any internal implementation details externally.

---

**Question 5.3 – API Request & Response Logging Filters**

Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?

**Answer:**  
The following are the advantages of using JAX_RS filter for cross-cutting, rather than manually inserting Logger.info():  

1. DRY Principle (Don't Repeat Yourself): Logging logic is defined once in LoggingFilter and automatically applied to every single request and response across all endpoints. With manual logging, identical boilerplate code must be copy-pasted into every resource method.  

2. Separation of Concerns: Resource methods should contain only business logic. Infrastructure concerns such as logging, authentication, and CORS belong in filters. Mixing them pollutes the business code and reduces readability.  

3. Guaranteed Coverage: A filter fires for every request without exception. Manual Logger calls are easily forgotten when a developer adds a new endpoint. With filters, new endpoints are automatically logged with no additional work required.  

4. Easy Maintenance: Changing the log format requires editing this one filter class. With manual logging, every resource method across the entire codebase would need to be individually updated.  

5. Error Path Coverage: Filters fire even when exceptions are thrown before the method body is reached. A Logger.info() call inside a method is completely skipped if an exception occurs before that line. Filters always execute, making logging complete and reliable.
