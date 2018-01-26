# OandaDataFetcher
A program I used to download data from the Oanda forex exchange.

# how to use
I only ran it from within my development enviroment, which worked fine for my purposes. You must create your own mysql database in order to download the data, as well as an account with Oanda to get your V3 authorization code.
Change the variables "url", "user", and "password" (found in Main.java) to your database and username/password.
Set the "Authorization" header value to your own authorization code.

Furthermore, you can change the "instrument" and "granularity" variables to fit your own use. It's by default set to "EUR_USD" and "M1".

