# A local proxy for Forex rates

## First thoughts
Initial first thoughts are that the best way to get around the API limitation of 1000 requests per one token 
per day is to cache the service every 5 minutes and request all the currency's every refresh 
(this can be done since you can concatenate all the currency pairs you want).

This makes it so you only have ```60 / 5 = 12``` requests per hour or ```(60 / 5) * 24 = 288``` requests per day 
which is below the daily limit of 1000 requests per day (this can be upped further but will be left at 5 minutes for now).

Taking a quick look through the code, I can see that Cats Effect 2 is still being used 

Things that can be added / refactored:
- Update cats effect to version 3 since version 2 is quite old (probably won't do as it requires a lot of possible refactoring)
- Adding cache system for functions ```get``` and ```set``` for every 5 minutes
- 