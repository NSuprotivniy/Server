# FEEDS


```
HashMap<long> subscribers 

```
##### save(json params)

```
    params: { title }
    return: { feed_id }
```
    
    
##### all()

```
    return: { feed_id[] }
```	
    
    
##### get(json params)

```
    params: { feed_id }
    return: { title }
```

##### find(json params)

```
    params: { title }
    		{ subscriber_id }
    
    return: { feed_id[] }
```

##### remove(json params)

```	
    params: { feed_id }
    return: { feed_id }
```
 
##### edit(json params)

```
    params: { feed_id, data { title } }
    return: { feed_id }
 ```
##### subscribers(json params)
```	
    params: { feed_id }
    return: { user_id[] }
```

# POSTS
    

##### save(json params)

```
    params: { title, body, feed_id, user_id }
    return: { post_id }
```
    
##### all()
```	
    return: { post_id[] }
```	
    
##### get(json params)
```	
    params: { post_id }
    return: { { title, body, user_name, feed_title, created_at} }
```
##### find(json params)
 
```
    params: { title }
    		{ subscriber_id }
            { feed_id }
            { created_at }
    
    return: { post_id[] }
```

##### where(json params)

```
    params: { title, subscriber_id, feed_id, period }
    
    return: { post_id[] }
```

##### remove(json params)

```
    params: { post_id }
    return: { post_id }
 ```
 
##### edit(json params)
```	
    params: { post_id, data { title, body } }
    return: { post_id }
```
    
# USERS

##### save(json params)

```
    params: { user_name }
    return: { user_id }
``` 
    
##### all()
```	
    return: { user_id[] }
```	
    
##### get(json params)
```	
    params: { user_id }
    return: { { user_name } }
```

##### find(json params)

```
    params: { user_name }
    return: { user_id[] }
```

##### remove(json params)

```	
    params: { user_id }
    return: { user_id }
 ```
 
##### edit(json params)
```	
    params: { user_id, data { user_name } }
    return: { user_id }
 ``` 
    
# SESSION

##### save(json params)

```	
    params: { user_id, client_address }
    return: { session_id }
```

##### get(json params)
```	
    params: { session_id }
    		{ client_address }
	return: { user_id, client_address, timestamp } 
```

##### remove(json params)

```	
    params:  { session_id }
    return : { session_id }
```

##### update_timestamp(json params)

Update last client request timestamp.

```
    params:  { session_id }
    return: { session_id }
 ```

##### clean()

Remove old sessions.

