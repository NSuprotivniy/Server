# Live Blog Server

---

## Description

It realizes server-side job of Live Blog on Java. 

## API

### Client-side application have access to Live Blog via client API.

You can connect to sever using preconfigured server address and port. 
For whole session you can use only one TCP connection to server.
For communication you should send JSON formated string of command and query content.
Responce formated with JSON too. 

### Examples of requests and responce format.

#### Post saving

Request

``` json
{  
   "cmd":"save_post",
   "content":{  
      "title":"Lorem Ipsum",
      "author":"Noname",
      "body":"Lorem ipsum dolor sit amet, consectetuer adipiscing elit."
   }
}
```

Response 
``` json
{  
   "cmd":"save_post",
   "result":"successful",
   "content":{  
      "title":"Lorem Ipsum",
      "author":"Marcus Tullius Cicero",
      "body":"Lorem ipsum dolor sit amet, consectetuer adipiscing elit."
   }
}
```

#### Getting all posts

Request

``` json
{  
   "cmd":"get_all_posts",
   "content":{}
}
```

Response

``` json
{  
   "cmd":"get_all_posts",
   "result":"successful",
   "content":{  
      "1":{  
         "title":"Lorem Ipsum",
         "author":"Marcus Tullius Cicero",
         "body":"Lorem ipsum dolor sit amet, consectetuer adipiscing elit.",
         "created_at":"2017-01-30 23:40:11"
      },
      "2":{  
         "title":"Principles",
         "author":"Vangelis Bibakis",
         "body":"Now principles discovered off increasing how reasonably middletons men. Add seems out man met plate court sense. His joy she worth truth given. All year feet led view went sake. You agreeable breakfast his set perceived immediate. Stimulated man are projecting favourable middletons can cultivated.",
         "created_at":"2017-01-20 00:40:07"
      },
      "3":{  
         "title":"Delightful",
         "author":"Vangelis Bibakis",
         "body":"Delightful remarkably mr on announcing themselves entreaties favourable. About to in so terms voice at. Equal an would is found seems of. The particular friendship one sufficient terminated frequently themselves. It more shed went up is roof if loud case. Delay music in lived noise an. Beyond genius really enough passed is up. ",
         "created_at":"2017-01-20 00:41:24"
      }
   }
}
```

#### Post subscribing

Request

``` json
{  
   "cmd":"subscribe_posts",
   "content":{  

   }
}
```

Response

``` json
{  
   "cmd":"subscribe_posts",
   "result":"successful",
   "content":{}
}
```

#### Getting last posts

Request

``` json
{  
   "cmd":"get_last_posts",
   "content":{  
      "period":"day",
      "amount":"7"
   }
}
```

Response

``` json
{  
   "cmd":"get_last_posts",
   "result":"successful",
   "content":{  
      "1":{  
         "title":"Lorem Ipsum",
         "author":"Marcus Tullius Cicero",
         "body":"Lorem ipsum dolor sit amet, consectetuer adipiscing elit.",
         "created_at":"2017-01-30 23:40:11"
      }   
   }
} 
```

#### Searching posts

Request

``` json
{  
   "cmd":"find_post",
   "content":{  
      "title":"Lorem Ipsum"
   }
}
```

Response

``` json
{  
   "cmd":"find_post",
   "result":"successful",
   "content":{  
      "1":{  
         "title":"Lorem Ipsum",
         "author":"Marcus Tullius Cicero",
         "body":"Lorem ipsum dolor sit amet, consectetuer adipiscing elit.",
         "created_at":"2017-01-30 23:18:58"
      }
   }
}
```
