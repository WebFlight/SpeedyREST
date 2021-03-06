# Mendix SpeedyREST module

Welcome to the Mendix SpeedyREST module. This module can be used in [Mendix](http://www.mendix.com) apps to improve the speed of REST published services. SpeedyREST created a wrapper around the Rest Services module in order to cache the response in the database. Binary content will not be cached, because the performance improvement is not significant.

![SpeedyREST logo][1]

## Related resources
* Rest Services on [Mendix App Store](https://appstore.home.mendix.com/link/app/997/Mendix/Rest-Services)

# Table of Contents

* [Getting Started](#getting-started)
* [Application](#application)
* [Technical implementation](#technical-implementation)
* [Development Notes](#development-notes)

# Getting started
1. Make sure to install and configure the [Rest Services](#related-resources) module for your published services.
2. The *SpeedyREST* module can be downloaded from within the Mendix Business Modeler in the Mendix Appstore into any model that is build with Mendix 7.0.2+.
3. Include the *JA_StartSpeedyREST* Java action in your start-up microflow after initialization of Rest Services. The /srest request handler will be added.
4. Integrate the ResponseCache_Overview snippet in a new page to get an overview of cached responses and configuration details.
5. Include the *IVK_ClearCacheKey* microflow at places in your application where objects are modified. This will make sure that the related cached responses are cleared and recreated.
6. Configure the constant *CACHE_TTL*. See the [configuration](#configuration) section for more details.
7. Access your REST service and replace /rest by /srest in your URL.

## Configuration
Configuration is done by modification of constants in the config folder in the *SpeedyREST* module.

| Constant | Description | Default value |
| ------------ | ------------- | ------------- |
|
| CACHE_TTL | Specify the TTL for cache entries in seconds. If set to 0, cache entries will never expire. | 86400 seconds (1 day) |

# Application
Response time of serving REST requests is likely to be slow, especially when REST requests involve:
* Retrieving many objects
* Retrieving objects over associations
* Retrieving using (multiple) XPaths
In some applications this is not desirable, especially when the response is used in webpages or apps the user wants to see instant response.

# Technical implementation
* The module adds the /srest request handler to the application. 
* Whenever a request is done at the /srest path, SpeedyREST checks whether the response of this path is already cached. If not, the REST module will be used to retrieve the response and feed it to the OutputStream (browser). The SpeedyResponse class, which implements the IMxRuntimeResponse interface, is feeded into the REST module and makes sure that everything written to the OutputStream, is stored in the database as a ResponseCache object. Objects will be cached only if the HTTP status is 200 and the request method is GET.
* If an object is found in the cache, the cached response will be send to the OutputStream. Headers and cookies are cached too and will be set identical to the output of the REST module the first time the first time the response was cached.

# Development notes
* Mendix security is not implemented. As a result, cached REST responses are publicly accessible.
* For contributions: fork this repository, make changes, fix/add unit tests in speedyrest.tests package and issue pull request.

 [1]: https://github.com/WebFlight/SpeedyREST/blob/master/docs/logo.png