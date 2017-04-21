# Mendix SpeedyREST module

Welcome to the Mendix SpeedyREST module. This module can be used in [Mendix](http://www.mendix.com) apps to improve the speed of REST published services. SpeedyREST created a wrapper around the Rest Services module in order to cache the response in the database.

## Related resources
* Rest Services on [Mendix App Store](https://appstore.home.mendix.com/link/app/997/Mendix/Rest-Services)

# Table of Contents

* [Getting Started](#getting-started)
* [Application](#application)
* [Development Notes](#development-notes)

# Getting started
1. Make sure to install and configure the [Rest Services](##related-resources) module for your published services.
2. The *SpeedyREST* module can be downloaded from within the Mendix Business Modeler in the Mendix Appstore into any model that is build with Mendix 7.0.2+.
3. Include the *JA_StartSpeedyREST* Java action in your start-up microflow after initialization of Rest Services. The /srest request handler will be added.
4. Integrate the ResponseCache_Overview snippet in a new page to get an overview of cached responses and configuration details.
5. Include the *IVK_ClearCacheKey* microflow at places in your application where objects are modified. This will make sure that the related cached responses are cleared and recreated.
6. Configure the constants *CACHE_FILE_CONTENT* and *CACHE_TTL*.
  * *CACHE_FILE_CONTENT* If is set to true, binary content (files and images) will be cached too. Be aware that this will consume additional database space. Default value: false.
  * *CACHE_TTL* Specify the TTL for cache entries in seconds. If set to 0, cache entries will never expire. Default value: 86400 seconds (1 day).

# Application


## Description

## Configuraton

# Development notes
* Unit tests are not yet finished. Work in progress.
* Mendix security is not yet implemented. As a result, cached REST responses are publicly accessible.