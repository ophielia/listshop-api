# Changelog - The List Shop - API

All notable changes to this project will be documented in this file.

## [1.12.7]  - July 2023

## [1.12.7]  - July 2023

## [1.12.7-1]  - July 2023 (adding limits to chart)

## [1.12.7-2]  - July 2023 (adding limits to chart)

## [1.12.7-3]  - July 2023 (adding limits to chart)

## [1.12.7-4]  - July 2023 (adding limits to chart)

### Fixed

- metrics

### Added

- screenshot profile

## [1.12.6]  - July 2023

### Changed

-- chart changes

## [1.12.5]  - June 2023

### Added

- overriding properties from config map

### Fixed

- query error with metrics

## [1.12.4]  - June 2023

### Added

- metrics for user counts

### Fixed

- removed unnecesary email from logs

## [1.12.3]  - June 2023

### Fixed

- Fixed bug in endpoint /tag/user for anonymous users

## [1.12.2]  - June 2023

### Changed

- TagController, AdminTagController now use userId directly and logs each endpoint

### Fixed

- Actuator liveness and readiness probes.
- frequency of authentication log

### Added

- prometheus endpoint to actuator

## [1.12.0]  - May 2023

### Added

- display order added to TagResource, short version

### Changed

- mail module now configured from list-service
- updated to SpringBoot 2.7
- static files configured to read from seperate component on live deployment
- logback used instead of log4j
- ListLayoutService and LayoutService consolidated into LayoutService
- ShoppingListController now uses userId directly and logs each endpoint
- ShoppingListService refactored to use userIds
- helmfiles changed for static files - use new separate static set

### Fixed

- test fixes, coverage improved
- small mail fixes

### Removed

- removed depracated code - tags

### Removed

- removed deprecated ListLayoutRestController

### Fixed

- some issues with mailing

## [1.11.1] February 2023

### Changed

- some configuration changed / fixed to run on kubernetes

## [1.11.0] January 2023

### Added

- endpoint to return standard tag to category mapping
- endpoint to return user tag to category mappings
- endpoint to post user tag to category mapping
- endpoint to retrieve categories available for user

### Changed

- tag not created if one already exists
- default rating returned if it doesn't exist for dish

### Deprecated

- subcategories in Category

### Removed

- all handling of subcategories for headers
- headers in DishRatingInfo
- a lot of dead code in ModelMapper

### Fixed

- some database issues, for tests and uitests


## [1.10.0] November 2022

### Added

- listlayouts now saved with default flag, and user id
- string length checks in UserController and Service

### Changed

- merge algorithm when client list is older than server.
- server ensures that a dish always has a dish type tag, by not deleting a dish tag for a dish if only one remains.

### Deprecated

- ListLayoutType still present, but no longer used.
- All (currently) unused methods in ListLayoutRestController set to depracated, and gutted.

### Removed

- code implementing (unused) subcategories for list layouts removed
- deprecated tag code (TagExtendedEntity)
- assign_select, search_select in TagEntity


## [1.9.0] June 2022

### Added

- user properties
- email for beta test
- listener which sends email for beta test

### Changed

- finished separation into api maven module

## [1.8.0] June 2022

### Added

- admin tag controller, for extranet functions
- new tag repository and dto for retrieving user specific tags

### Changed

- reworked tag system
- recursive query used to get tag structure
- tag group is saved in db rather than calculated
- tags created for users
- many tag endpoints moved to admin controller
- created tags are attached to user
- autotag runs less often

### Deprecated

- tag_search_groups - calculated on the fly now with the help of recursive queries

### Fixed

- dish search
- autotag functions

## [1.7.3,4,5] April 2022

### Changed

- Fiddling with actuator

## [1.7.2] April 2022

### Changed

- Fixing actuator

## [1.7.1] April 2022

### Changed

- Unit tests for TargetService
- Integration tests for Shopping List

### Deprecated

- TagInfoRestController - will be removed in next release

### Fixed

- bug - selective cross off removed all
- bug - removing dish from list "activated" items which were crossed off

## [1.7.0] February 2022

### Added

- configured app to register with actuator admin app
- end point user/client/version
- configuration for password reset email completed

### Changed

- check user name endpoint email moved to payload
- all emails converted to lower case in back end
- logging configuration. Fewer automatic logs. Adding application logs.
- 
### Fixed

- bug updating item count in list
- bug in which tags weren't returned for dishes

## [1.6.0] February 2022

### Added

* end point to delete user
* end point to copy meal plan
* mail service module

### Changed

* Adding dish to meal plan ignored if meal plan already contains dish

## [1.5.3] February 2022

### Fixed

* corrected json returned for meal plan list

## [1.5.2] January 2022

### Changed

* continuing multi-module in maven

## [1.5.1]

(skipped)

## [1.5.0] November 2021

### Changed

* beginning to multi-module in maven

### Deprecated

* TagInfoRestController

### Fixed
 * Login in one browser doesn't kick out of other browsers

### Added
 * Task to clear exipired tokens

## [1.4.3] November 2021

### Fixed

- test fixes

## [1.4.2] October 2021

### Added

- new method in DishRestController which sets rating to a specific step: POST /dish/{dishId}/rating/{ratingId}/{step}

### Changed

- tests for ShoppingListService and TagService moved to mocks

### Deprecated

 - TagInfoController, previously used to accessed structured tag information, is now depracated. TagRestController method GET /tags with the option extended should be used instead to access the same information.

### Fixed
 
 - No longer possible to delete last list for user

## [1.4.1] July 2021

### Added

- created new custom repository to handle statistic creation and management

### Changed

- tests moved from db integration tests to mock tests

## [1.4.0] July 2021

### Changed

* Using testcontainers rather than local database for tests
* Moving some tests from IT tests to Mock tests

### Added

* sort parameters to endpoint GET /dish
* name fragment parameter to GET /dish

## [1.3.2] - June 2021

## Changed

- removed field injection from several services
- squashed flyway migrations

## [1.3.1] - April 2021

## Changed

- several services use constructor autowiring rather than field autowiring

## [1.3.0]  January 2, 2021

### Added

* new endpoint POST /shoppinglist/dish with ListAddProperties which adds multiple dishes to a lsit at the same time.
* workflow to repository to build and push docker image upon release.

### Changed

* Upgrade to Java 11
* Upgrade to spring boot 2.2.2.RELEASE

### Fixed
* bug in which crossed off items were not copied as crossed off
* bug in which updates to list items didn't take list or dish context into account.
* many small refactors

## [1.2.2]  August 1, 2020

### Added
* Configuration to serve static resources
  - from server directory /opt/com.meg.listshop.listshop/static
  - served from http://<root>/static
  
### Changed
* Exception AuthenticationException now returned if user 
token has expired

### Fixed
* bug in which RatingTagProcessor broke if no rating tags available
* bug in add from list.  UsedCount now correctly updated.


## [1.2.0] June 9, 2020

### Changed
*   ShoppingList api object changed
    -  ShoppingList returns sources in Legend_Sources. 
    -  Item api object now return source keys only in new field sourc_keys.

### Deprecated
* In ShoppingList api object
    -  Dish_sources and list_sources are depracated in ShoppingList api object
    -  Dish_sources and list_sources are depracated in Item api object

### Added
* new table user_devices to hold device information and token per device for user.
* logout method which removes device from db.

### Changed

* tokens generated on authentication are now persisted to the db.
* authentication verifies according to token saved in db.

### Fixed

* bug with tag_relationships having same child and parent corrected.

## [1.1.1] - baseline

### Added

### Changed

### Deprecated

### Fixed

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
