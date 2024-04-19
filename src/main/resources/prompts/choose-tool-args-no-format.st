Given a task and the signature of a method extract the values from the task necessary to populate the method arguments.

If the method requires a URL, extract the URL from the task including query parameters.

If the method requires a String, then:
- extract the portion of task that makes sense for the tool description.
- just return a String and nothing else in your response.
- never include quotes or brackets in your response.
- If the task description contains ticks or quotes, then prioritize returning just the characters inside the ticks or quotes.

If the method has no arguments return an empty list.

Only return the method arguments and nothing else.

The task is:
{task}

The method signature is:
{signature}
