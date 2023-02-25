# A local proxy for Forex rates

## First thoughts
Initial first thoughts are that the best way to get around the API limitation of 1000 requests per one token 
per day is to cache the service every 5 minutes and request all the currency's every refresh 
(this can be done since you can concatenate all the currency pairs you want).

This makes it, so you only have ```60 / 5 = 12``` requests per hour or ```(60 / 5) * 24 = 288``` requests per day 
which is below the daily limit of 1000 requests per day (this can be upped further but will be left at 5 minutes for now).

Taking a quick look through the code, I can see that Cats Effect 2 is still being used

The general plan for now seems to be:
- Adding cache system for functions ```get``` for every 5 minutes
- Start program and ping all pairs and store them in a map and check if cache is older than 5 mins whenever someone pings the service
  - if not then returns cached pairs
  - if so then it updates the cache map with all the pairs then returns the pairs

---

Struggles right now / that I had:
- Not too used to Circe and not used to Pureconfig, so I spent a LOT of time trying to figure those out (had wasted a few hours on TimeStamp using a capital "S" which I accidentally changed to a lowercase "s")
- Had to deal with ```errors.Error``` wasting a lot of my time since ```Error``` is a top level keyword which lead me to getting type mismatch all the time, fixed this by changing it to ```AppError```

Things that can be added / refactored:
- Update cats effect to version 3 since version 2 is quite old (probably won't do as it requires a lot of possible refactoring)
- More unit tests can be added since there aren't any which can obviously lead to faulty code (*I would libraries to test such as weaver*)
- Definitely can **add more error checking** in the cache and request handling, right now there isn't much for the cache itself however if I could further improve on this then I would extend the AppErrors to deal with the cache and requests having more detailed errors
  

- **Examples** of additional error handling that I can add are:
  - For the **cache**, if you give invalid/duplicated currencies, you would get a ```NoSuchElementException```. 
    - I would deal with this error by either having some sort of error
checking inside of ```Currency.scala``` (probably using an Enum or having ```_``` in the case match) or checking when accessing the cache itself. There are a few other solutions but they are probably not as elegant.
  - This also extends to the **Currency.scala** case matching throwing a ```MatchError``` when using an invalid currency which can be dealt with as I said above
  - For the **requests** side, if OneFrame is inaccessible, then there isn't any proper error handling involved to deal with no response, leading to an ```IOException```
    - I would deal with this error by having checks if there are any other response besides ```Ok``` or ```200``` 
so if we do get a response (such as ```503 Service Unavailable```) we can properly handle and let the downstream user's know any error's that are useful. 
    - Otherwise, if there is no response, then we can respond with some error message so the downstream user can know that OneFrame itself is down
