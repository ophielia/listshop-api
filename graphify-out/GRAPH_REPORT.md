# Graph Report - listshop-api  (2026-09-10)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 7205 nodes · 24153 edges · 187 communities (134 shown, 48 thin omitted)
- Extraction: 78% EXTRACTED · 22% INFERRED · 0% AMBIGUOUS · INFERRED: 5332 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `dd8bf7b7`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- org.slf4j.Logger
- com.fasterxml.jackson.annotation.JsonIgnore
- ShoppingListEntity
- UnitEntity
- org.springframework.http.ResponseEntity
- ListItemDetailEntity
- org.springframework.test.context.ActiveProfiles
- DishTagSearchResult
- jakarta.servlet.http.HttpServletRequest
- .toModel
- .toModel
- DishItemEntity
- MealPlanEntity
- CollectorContext
- CollectedItem
- UserEntity
- org.junit.jupiter.api.Test
- FactorCriteria
- .authToken
- Tag
- .getUnit
- TagEntity
- ListItemEntity
- RatingInfo
- ConvertibleAmount
- ProposalEntity
- UserRestController
- LegacyShoppingListService
- FractionType
- ListLayoutCategoryEntity
- ListLayoutEntity
- .getId
- ProcessingContext
- TargetEntity
- DishEntity
- Tag
- ClientDeviceInfo
- org.springframework.data.jpa.repository.Query
- AdminTagFullInfo
- UnitType
- .toEntity
- UserPropertyEntity
- AdminTagRestControllerApi
- ShoppingListServiceImpl
- .getUserByUserEmail
- ConversionFactor
- SimpleListItemDTO
- .getDishForUserById
- AdminUser
- MealPlan
- com.fasterxml.jackson.annotation.JsonInclude
- Category
- ProposalSlotEntity
- .testUpdateIngredientInDish_ConversionNewMarkers
- TagSearchCriteria
- NestedTag
- SuggestionDTO
- ProposalRequest
- LayoutCategoryResource
- TagType
- LegacyShoppingListRestControllerTest
- DomainType
- .getId
- TargetService
- DishSearchCriteria
- DishRestController
- UserDeviceEntity
- ItemMappingDTO
- org.springframework.context.annotation.Bean
- ShoppingListItem
- DishItemDTO
- AutoTagInstructionEntity
- Instruction
- jakarta.persistence.EntityManager
- IngredientPut
- ShoppingListItem
- FoodConversionEntity
- ProposalGeneratorServiceImplTest
- LayoutCategoryDTO
- TagServiceImpl
- ProcessInformation
- TagOperationPut
- ListLayoutCategory
- ConversionFactorEntity
- org.springframework.stereotype.Component
- FoodEntity
- ListShopCategory
- Dish
- Suggestion
- RestResponseExceptionHandler
- ShoppingListProperties
- AutoTagSubject
- MailConfiguration
- ShoppingList
- LongTagIdPairDTO
- FoodCategoryResource
- TokenEntity
- ListItemSource
- SimpleFoodFactor
- EmailParameters
- ListLayoutCategory
- JwtService
- ConversionContext
- MergeItem
- FoodMappingDTO
- .getUsername
- FoodCategoryEntity
- User
- ConversionBridgeFactorEntity
- CampaignEntity
- .makeUSUnit
- FoodCategoryMappingEntity
- CustomStatisticRepository
- TagReplaceServiceImpl
- SecurityConfig.java
- BasicAmount
- DishRestControllerTest
- MappingPost
- MergeResult
- Dish
- Statistic
- ListTagStatistic
- PostSearchTags
- TargetResource
- FlatStringUtils
- Amount
- V2DishRestControllerTest
- JwtServiceImpl
- NestedDish
- .getProposalOrders
- freemarker.template.Configuration
- ShoppingListCategory
- ListTagStatisticServiceImpl
- MailService
- ProposalProcessor
- ListOperationType
- Ingredient
- NestedShoppingList
- CustomUserRepositoryImpl
- TagRestControllerTest
- MergeRequest
- DishDTO
- StatisticListPost
- PutDish
- Initializer
- RoundingType
- TagConverterProcessor
- .setItems
- .categorizeList
- AdminUserDetailsEntity
- .getUnitSubtype
- ShoppingListPut
- DomainConverterProcessor
- CustomListLayoutRepositoryImpl.java
- CustomUserDetails
- ContentMap
- RatingUpdateInfoResource
- ItemSource
- ShoppingListPut
- com.meg.listshop:listshop-service
- UserMetricsTask
- DateUtils
- JwtAuthenticationRequest
- StandardUserTagConflictDTO
- Application.java
- CollectedItemStatus
- Role
- .createGroupSubquery
- ListItemState
- TagSwapout
- SharedPropertyKeys
- JwtAuthenticationResponse
- Dummy
- TargetProposalConfigurationImpl.java
- ListLayoutException
- copystaticfiles.sh
- 2.baseline_command.sh
- com.meg.listshop:common
- com.meg.listshop:listshop-conversion
- com.meg.listshop:listshop-lmt
- com.meg.listshop:listshop-parent
- listshop-api

## God Nodes (most connected - your core abstractions)
1. `TagEntity` - 293 edges
2. `ListItemEntity` - 226 edges
3. `UnitEntity` - 180 edges
4. `UserEntity` - 129 edges
5. `ConvertibleAmount` - 122 edges
6. `ShoppingListEntity` - 116 edges
7. `DishEntity` - 114 edges
8. `ProcessingContext` - 103 edges
9. `ListItemDetailEntity` - 103 edges
10. `ListShopPostgresqlContainer` - 102 edges

## Surprising Connections (you probably didn't know these)
- `BaseShoppingListService` --references--> `ItemProcessingException`  [EXTRACTED]
  listshop-lmt/listshop-service/src/main/java/com/meg/listshop/lmt/list/BaseShoppingListService.java → listshop-api/src/main/java/com/meg/listshop/lmt/api/exception/ItemProcessingException.java
- `LegacyShoppingListServiceImpl` --references--> `ItemProcessingException`  [EXTRACTED]
  listshop-lmt/listshop-service/src/main/java/com/meg/listshop/lmt/list/impl/LegacyShoppingListServiceImpl.java → listshop-api/src/main/java/com/meg/listshop/lmt/api/exception/ItemProcessingException.java
- `ShoppingListServiceImpl` --references--> `ItemProcessingException`  [EXTRACTED]
  listshop-lmt/listshop-service/src/main/java/com/meg/listshop/lmt/list/v2/impl/ShoppingListServiceImpl.java → listshop-api/src/main/java/com/meg/listshop/lmt/api/exception/ItemProcessingException.java
- `AdminLayoutRestController` --implements--> `AdminLayoutRestControllerApi`  [EXTRACTED]
  listshop-lmt/listshop-service/src/main/java/com/meg/listshop/admin/controller/AdminLayoutRestController.java → listshop-api/src/main/java/com/meg/listshop/admin/controller/AdminLayoutRestControllerApi.java
- `AdminTagRestController` --implements--> `AdminTagRestControllerApi`  [EXTRACTED]
  listshop-lmt/listshop-service/src/main/java/com/meg/listshop/admin/controller/AdminTagRestController.java → listshop-api/src/main/java/com/meg/listshop/admin/controller/AdminTagRestControllerApi.java

## Import Cycles
- None detected.

## Communities (187 total, 48 thin omitted)

### Community 0 - "org.slf4j.Logger"
Cohesion: 0.03
Nodes (71): StringTools, jakarta.transaction.Transactional, java.net.MalformedURLException, ActionIgnoredException, ActionInvalidException, AuthenticationException, BadParameterException, ItemProcessingException (+63 more)

### Community 1 - "com.fasterxml.jackson.annotation.JsonIgnore"
Cohesion: 0.02
Nodes (36): com.fasterxml.jackson.annotation.JsonIgnore, Override, ProposalResource, Override, UserPropertiesResource, Override, UserProperty, Override (+28 more)

### Community 2 - "ShoppingListEntity"
Cohesion: 0.04
Nodes (20): ItemOperationType, Copy, CrossOff, Move, Remove, RemoveAll, RemoveCrossedOff, UnCrossOff (+12 more)

### Community 3 - "UnitEntity"
Cohesion: 0.05
Nodes (34): Entity, Override, Table, UnitEntity, UnitRepository, RoundingUtils, SpecificationType, ALL (+26 more)

### Community 4 - "org.springframework.http.ResponseEntity"
Cohesion: 0.04
Nodes (54): RequestMapping, RestController, ProposalRestControllerApi, DeleteMapping, DishRestControllerApi, CrossOrigin, DeleteMapping, Dish (+46 more)

### Community 5 - "ListItemDetailEntity"
Cohesion: 0.04
Nodes (22): Entity, NamedEntityGraph, Override, Table, ListItemDetailEntity, ListConversionService, AbstractTransition, ProcessingType (+14 more)

### Community 6 - "org.springframework.test.context.ActiveProfiles"
Cohesion: 0.09
Nodes (61): com.fasterxml.jackson.databind.ObjectMapper, io.restassured.response.Response, jakarta.annotation.PostConstruct, ProposalContextRepository, Application, AdminTagRestControllerTest, AdminUserRestControllerTest, AuthenticationRestControllerTest (+53 more)

### Community 7 - "DishTagSearchResult"
Cohesion: 0.03
Nodes (21): ApproachType, REV_SORTED_WHEEL, SORTED_WHEEL, WHEEL, WHEEL_MIXED, ContextApproachEntity, Entity, Table (+13 more)

### Community 8 - "jakarta.servlet.http.HttpServletRequest"
Cohesion: 0.04
Nodes (39): jakarta.servlet.http.HttpServletRequest, java.net.URI, java.security.Principal, AuthenticationRestControllerApi, CampaignControllerApi, CrossOrigin, PostMapping, RequestMapping (+31 more)

### Community 9 - ".toModel"
Cohesion: 0.04
Nodes (22): SourceReferenceType, DISH, LIST, CategoryDTO, Override, Override, ListItemDTO, Override (+14 more)

### Community 10 - ".toModel"
Cohesion: 0.03
Nodes (16): ConversionGrid, Override, ConversionSample, Override, Food, FoodCategoryMapping, Override, Override (+8 more)

### Community 11 - "DishItemEntity"
Cohesion: 0.12
Nodes (9): DishItemEntity, Entity, Override, Table, StateMachineActiveTransitionTest, RemovalTestsNoAmounts, RemovalTestsWithAmounts, StateMachineRemovedTransitionTest (+1 more)

### Community 12 - "MealPlanEntity"
Cohesion: 0.06
Nodes (16): MealPlanType, Manual, Targeted, Entity, NamedEntityGraph, Table, MealPlanEntity, Entity (+8 more)

### Community 13 - "CollectorContext"
Cohesion: 0.05
Nodes (16): ContextType, List, Merge, NonSpecified, StatisticCountType, List, None, Single (+8 more)

### Community 14 - "CollectedItem"
Cohesion: 0.07
Nodes (5): AbstractItemCollector, Override, CollectedItem, Override, MergeItemCollector

### Community 15 - "UserEntity"
Cohesion: 0.05
Nodes (17): UserCreateException, AuthorityEntity, Entity, Table, AuthorityName, ROLE_ADMIN, ROLE_USER, Entity (+9 more)

### Community 16 - "org.junit.jupiter.api.Test"
Cohesion: 0.04
Nodes (5): RoundingUtilsTest, ObjectMapper, MealPlanRestControllerTest, org.junit.jupiter.api.Test, org.springframework.security.test.context.support.WithMockUser

### Community 17 - "FactorCriteria"
Cohesion: 0.04
Nodes (7): Join, FactorCriteria, CustomConversionFactorRepositoryImpl, CriteriaBuilder, CriteriaQuery, Override, Root

### Community 18 - ".authToken"
Cohesion: 0.09
Nodes (12): Header, io.restassured.http.Header, DeleteListTests, BeforeEach, Disabled, Nested, ShoppingList, ShoppingListItem (+4 more)

### Community 19 - "Tag"
Cohesion: 0.04
Nodes (5): com.fasterxml.jackson.annotation.JsonProperty, TargetProposal, TargetProposalSlot, Tag, TargetSlot

### Community 21 - "TagEntity"
Cohesion: 0.04
Nodes (14): Entity, NamedEntityGraph, NamedNativeQuery, Override, SqlResultSetMapping, Table, TagEntity, ListSearchServiceImpl (+6 more)

### Community 22 - "ListItemEntity"
Cohesion: 0.06
Nodes (11): Entity, NamedEntityGraphs, Override, Table, ListItemEntity, AmountTextBuilder, Override, ListConversionServiceImpl (+3 more)

### Community 23 - "RatingInfo"
Cohesion: 0.04
Nodes (13): DishRatingInfo, Override, Override, RatingInfo, RatingUpdateInfo, DishRating, Override, DishRatings (+5 more)

### Community 24 - "ConvertibleAmount"
Cohesion: 0.06
Nodes (11): AddScaleRequest, Override, ConversionSampleDTO, ConvertibleAmount, ConverterServiceImpl, Override, ConverterProcessor, BaseTagHandler (+3 more)

### Community 25 - "ProposalEntity"
Cohesion: 0.07
Nodes (11): Entity, Table, ProposalContextEntity, Entity, Table, ProposalEntity, ProposalRepository, Override (+3 more)

### Community 26 - "UserRestController"
Cohesion: 0.05
Nodes (16): CrossOrigin, GetMapping, PostMapping, RequestMapping, RestController, UserRestControllerApi, ClientVersions, ListShopPayload (+8 more)

### Community 27 - "LegacyShoppingListService"
Cohesion: 0.06
Nodes (14): EmbeddedShoppingListListResource, ShoppingListListResource, ShoppingListResource, MergeRequest, Override, ResponseEntity, ShoppingList, ShoppingListCategory (+6 more)

### Community 28 - "FractionType"
Cohesion: 0.04
Nodes (18): doubleValueOf(), FractionType, FiveEighths, One, OneEighth, OneHalf, OneQuarter, OneThird (+10 more)

### Community 29 - "ListLayoutCategoryEntity"
Cohesion: 0.10
Nodes (6): Entity, NamedNativeQuery, Table, ListLayoutCategoryEntity, Override, LayoutServiceImplMockTest

### Community 30 - "ListLayoutEntity"
Cohesion: 0.06
Nodes (10): Entity, NamedEntityGraph, Table, ListLayoutEntity, ListLayoutRepository, BaseLayoutServiceImpl, Override, LegacyLayoutServiceImpl (+2 more)

### Community 31 - ".getId"
Cohesion: 0.06
Nodes (6): ItemOperationPut, Override, ListAddProperties, Override, ShoppingListRestController, ShoppingListService

### Community 32 - "ProcessingContext"
Cohesion: 0.09
Nodes (9): FactorProvider, ProcessingContext, ProcessingUtils, Override, Override, ScalingProcessor, BaseScaleHandler, ScaleHandler (+1 more)

### Community 33 - "TargetEntity"
Cohesion: 0.07
Nodes (14): TargetType, PickUp, Standard, AbstractInflateAndFlatten, Entity, Table, TargetEntity, Entity (+6 more)

### Community 34 - "DishEntity"
Cohesion: 0.07
Nodes (9): DishEntity, Entity, NamedEntityGraphs, Table, DishRepository, DishMapper, DishServiceImpl, Override (+1 more)

### Community 35 - "Tag"
Cohesion: 0.04
Nodes (19): GetMapping, PostMapping, PutMapping, RequestMapping, RestController, TagRestControllerApi, GetMapping, PostMapping (+11 more)

### Community 36 - "ClientDeviceInfo"
Cohesion: 0.07
Nodes (4): ClientDeviceInfo, Override, JwtAuthorizationRequest, PutCreateUser

### Community 37 - "org.springframework.data.jpa.repository.Query"
Cohesion: 0.06
Nodes (9): ItemToCategoryDTO, DishItemRepository, ItemRepository, TargetSlotRepository, org.springframework.data.domain.Pageable, org.springframework.data.jpa.repository.EntityGraph, org.springframework.data.jpa.repository.JpaRepository, org.springframework.data.jpa.repository.Modifying (+1 more)

### Community 38 - "AdminTagFullInfo"
Cohesion: 0.05
Nodes (4): AdminTagFullInfo, Override, AdminTagFullInfoResource, Override

### Community 39 - "UnitType"
Cohesion: 0.07
Nodes (25): UnitSubtype, LIQUID, NONE, SOLID, VOLUME, WEIGHT, findByName(), UnitType (+17 more)

### Community 40 - ".toEntity"
Cohesion: 0.05
Nodes (4): Item, Override, Target, Tag

### Community 41 - "UserPropertyEntity"
Cohesion: 0.09
Nodes (9): Entity, Override, Table, UserPropertyEntity, UserPropertyRepository, Override, UserPropertyServiceImpl, UserPropertyChangeListener (+1 more)

### Community 42 - "AdminTagRestControllerApi"
Cohesion: 0.06
Nodes (14): AdminTagRestControllerApi, DeleteMapping, GetMapping, PostMapping, PutMapping, RequestMapping, RestController, Tag (+6 more)

### Community 43 - "ShoppingListServiceImpl"
Cohesion: 0.07
Nodes (5): ItemMergeDTO, Override, MergeResult, Override, ShoppingListServiceImpl

### Community 45 - "ConversionFactor"
Cohesion: 0.09
Nodes (5): ConversionFactor, Override, SimpleConversionFactor, CustomConversionFactorRepository, TagContextScaler

### Community 46 - "SimpleListItemDTO"
Cohesion: 0.07
Nodes (4): Override, PostListItem, Override, SimpleListItemDTO

### Community 47 - ".getDishForUserById"
Cohesion: 0.07
Nodes (7): SortOrMoveDirection, DOWN, UP, ICountResult, TagType, CountResult, Override

### Community 48 - "AdminUser"
Cohesion: 0.05
Nodes (11): AdminUserRestControllerApi, GetMapping, PostMapping, RequestMapping, RestController, AdminUser, Override, AdminUserListResource (+3 more)

### Community 49 - "MealPlan"
Cohesion: 0.06
Nodes (7): EmbeddedMealPlanListResource, MealPlan, Override, MealPlanListResource, Override, MealPlanResource, Slot

### Community 50 - "com.fasterxml.jackson.annotation.JsonInclude"
Cohesion: 0.05
Nodes (6): com.fasterxml.jackson.annotation.JsonInclude, com.fasterxml.jackson.annotation.JsonPropertyOrder, Override, LegendSource, ShoppingList, ShoppingListCategory

### Community 52 - "Category"
Cohesion: 0.05
Nodes (6): Category, Override, Item, MergeRequest, ItemCategoryPojo, Override

### Community 53 - "ProposalSlotEntity"
Cohesion: 0.07
Nodes (6): DishSlotEntity, Entity, Table, Entity, Table, ProposalSlotEntity

### Community 55 - "TagSearchCriteria"
Cohesion: 0.06
Nodes (18): TagFilterType, All, GroupsOnly, NoGroups, ParentTags, ToReview, IncludeType, EXCLUDE (+10 more)

### Community 56 - "NestedTag"
Cohesion: 0.06
Nodes (6): Dish, Override, NestedTag, Override, RatingInfo, RatingInfo

### Community 57 - "SuggestionDTO"
Cohesion: 0.06
Nodes (15): ModifierType, Marker, Unit, UnitSize, Entity, Override, SqlResultSetMapping, Table (+7 more)

### Community 58 - "ProposalRequest"
Cohesion: 0.19
Nodes (5): Override, ProposalRequest, ProposalRequestBuilder, FillInProposalProcessorImplTest, ProcessorTestUtils

### Community 59 - "LayoutCategoryResource"
Cohesion: 0.06
Nodes (14): AdminLayoutRestControllerApi, GetMapping, PutMapping, RequestMapping, RestController, EmbeddedLayoutCategoryListResource, Override, LayoutCategory (+6 more)

### Community 60 - "TagType"
Cohesion: 0.07
Nodes (13): Override, PostUpdateTags, TagType, DishType, Ingredient, NonEdible, Rating, TagType (+5 more)

### Community 62 - "DomainType"
Cohesion: 0.07
Nodes (15): DomainType, ALL, METRIC, UK, US, findByName(), AmountRepository, TokenRepository (+7 more)

### Community 63 - ".getId"
Cohesion: 0.09
Nodes (6): Entity, Table, TagRelationEntity, TagRelationRepository, Override, TagStructureServiceImpl

### Community 64 - "TargetService"
Cohesion: 0.09
Nodes (4): Override, ResponseEntity, TargetRestController, TargetService

### Community 65 - "DishSearchCriteria"
Cohesion: 0.07
Nodes (13): DishSort, DishSortDirection, ASC, DESC, DishSortKey, CreatedOn, LastUsed, Name (+5 more)

### Community 66 - "DishRestController"
Cohesion: 0.08
Nodes (9): DishListResource, Override, DishResource, Override, EmbeddedDishListResource, DishRestController, Dish, Override (+1 more)

### Community 67 - "UserDeviceEntity"
Cohesion: 0.08
Nodes (8): ClientType, Mobile, Web, Entity, Override, Table, UserDeviceEntity, UserDeviceRepository

### Community 68 - "ItemMappingDTO"
Cohesion: 0.05
Nodes (4): ItemMappingDTO, Override, ShoppingListItem, ListMappingCustomRepository

### Community 69 - "org.springframework.context.annotation.Bean"
Cohesion: 0.10
Nodes (20): io.swagger.v3.oas.models.OpenAPI, Configuration, FreeMarkerConfigurer, PostofficeConfiguration, SpringDocConfiguration, TestConfiguration, WebConfiguration, ListLayoutProperties (+12 more)

### Community 70 - "ShoppingListItem"
Cohesion: 0.05
Nodes (4): Override, ShoppingListItem, Override, ShoppingListItemDetails

### Community 71 - "DishItemDTO"
Cohesion: 0.07
Nodes (5): DishItemDTO, Override, CustomDishItemRepository, CustomDishItemRepositoryImpl, EntityManager

### Community 72 - "AutoTagInstructionEntity"
Cohesion: 0.08
Nodes (10): DiscriminatorColumn, Inheritance, AutoTagInstructionEntity, Entity, Table, Override, TagInstructionEntity, Override (+2 more)

### Community 73 - "Instruction"
Cohesion: 0.09
Nodes (9): jakarta.persistence.DiscriminatorValue, TextInstructionEntity, Instruction, TextInstructionRepository, Override, TextProcessorImpl, AbstractAutoTagProcessor, Override (+1 more)

### Community 74 - "jakarta.persistence.EntityManager"
Cohesion: 0.12
Nodes (18): jakarta.persistence.EntityManager, java.sql.ResultSet, javax.sql.DataSource, CustomAmountRepositoryImpl, NamedParameterJdbcTemplate, CustomTagRepositoryImpl, Override, ItemMappingDTOMapper (+10 more)

### Community 75 - "IngredientPut"
Cohesion: 0.10
Nodes (6): IngredientPut, Override, DishRestController, Dish, Override, ResponseEntity

### Community 76 - "ShoppingListItem"
Cohesion: 0.05
Nodes (4): Tag, ShoppingListItem, Override, ShoppingListItemComparator

### Community 78 - "ProposalGeneratorServiceImplTest"
Cohesion: 0.14
Nodes (4): ProposalGeneratorService, Override, ProposalService, ProposalGeneratorServiceImplTest

### Community 79 - "LayoutCategoryDTO"
Cohesion: 0.08
Nodes (5): CategoryTagMapping, Override, LayoutCategoryDTO, Override, LayoutDTO

### Community 80 - "TagServiceImpl"
Cohesion: 0.10
Nodes (3): Override, TagType, TagServiceImpl

### Community 82 - "TagOperationPut"
Cohesion: 0.07
Nodes (10): TagOperationPut, TagOperationType, AssignFood, AssignFoodCategory, AssignToUser, CopyFoodFromTag, CopyToStandard, MarkAsNoFoodVerified (+2 more)

### Community 83 - "ListLayoutCategory"
Cohesion: 0.07
Nodes (5): Override, ListLayout, ListLayoutCategory, Override, ListLayoutList

### Community 84 - "ConversionFactorEntity"
Cohesion: 0.11
Nodes (5): ConversionFactorEntity, Entity, Override, Table, Override

### Community 85 - "org.springframework.stereotype.Component"
Cohesion: 0.18
Nodes (12): ConversionFactorRepository, FactorCriteriaBuilder, ConversionTarget, ContextScaler, GenericScaler, SingleUnitScaler, TagUnitScaler, UnitScaler (+4 more)

### Community 87 - "ListShopCategory"
Cohesion: 0.09
Nodes (5): AbstractCategory, Override, Override, ListLayoutCategoryPojo, ListShopCategory

### Community 88 - "Dish"
Cohesion: 0.07
Nodes (8): TargetProposalDish, Dish, ItemSourceType, Dish, List, Special, Dish, UserAccount

### Community 89 - "Suggestion"
Cohesion: 0.09
Nodes (10): AmountRestControllerApi, GetMapping, RequestMapping, RestController, EmbeddedSuggestionListResource, Override, Suggestion, Override (+2 more)

### Community 90 - "RestResponseExceptionHandler"
Cohesion: 0.15
Nodes (8): ApiError, ResponseEntity, RestResponseExceptionHandler, org.springframework.security.authentication.BadCredentialsException, org.springframework.web.bind.annotation.ControllerAdvice, org.springframework.web.bind.annotation.ExceptionHandler, org.springframework.web.context.request.WebRequest, org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

### Community 91 - "ShoppingListProperties"
Cohesion: 0.09
Nodes (7): CategoryType, Frequent, Highlight, HighlightList, Standard, UnCategorized, ShoppingListProperties

### Community 92 - "AutoTagSubject"
Cohesion: 0.12
Nodes (4): Entity, Table, ShadowTags, AutoTagSubject

### Community 95 - "LongTagIdPairDTO"
Cohesion: 0.08
Nodes (7): LongTagIdPairDTO, CustomTagRelationRepository, CustomTagRepository, CustomTagRelationRepositoryImpl, NamedParameterJdbcTemplate, LongTagIdPairMapper, TagConflictMapper

### Community 96 - "FoodCategoryResource"
Cohesion: 0.10
Nodes (7): EmbeddedFoodCategoryListResource, FoodCategory, Override, FoodCategoryListResource, Override, FoodCategoryResource, Override

### Community 97 - "TokenEntity"
Cohesion: 0.15
Nodes (6): TokenType, PasswordReset, Entity, Table, TokenEntity, TokenService

### Community 99 - "SimpleFoodFactor"
Cohesion: 0.09
Nodes (3): Override, SimpleFoodFactor, FoodFactor

### Community 100 - "EmailParameters"
Cohesion: 0.23
Nodes (3): Override, EmailParameters, MailServiceTest

### Community 101 - "ListLayoutCategory"
Cohesion: 0.08
Nodes (3): Override, ListLayout, ListLayoutCategory

### Community 102 - "JwtService"
Cohesion: 0.15
Nodes (15): jakarta.servlet.Filter, jakarta.servlet.FilterChain, jakarta.servlet.http.HttpServletResponse, JwtAuthFilter, Override, JwtAuthFilterDummyImpl, Override, JwtAuthFilterImpl (+7 more)

### Community 105 - "FoodMappingDTO"
Cohesion: 0.10
Nodes (7): FoodMappingDTO, Override, CustomFoodMappingRepository, CustomFoodMappingRepositoryImpl, FoodMappingMapper, NamedParameterJdbcTemplate, Override

### Community 106 - ".getUsername"
Cohesion: 0.18
Nodes (6): Override, ResponseEntity, MealPlanRestController, Override, ResponseEntity, ProposalRestController

### Community 107 - "FoodCategoryEntity"
Cohesion: 0.12
Nodes (5): jakarta.persistence.Entity, jakarta.persistence.Table, FoodCategoryEntity, Override, org.hibernate.annotations.Immutable

### Community 108 - "User"
Cohesion: 0.10
Nodes (3): User, Override, UserResource

### Community 109 - "ConversionBridgeFactorEntity"
Cohesion: 0.08
Nodes (4): ConversionBridgeFactorEntity, Entity, Override, Table

### Community 110 - "CampaignEntity"
Cohesion: 0.10
Nodes (7): CampaignEntity, Entity, Override, Table, CampaignRepository, CampaignServiceImpl, Override

### Community 112 - "FoodCategoryMappingEntity"
Cohesion: 0.12
Nodes (5): FoodCategoryMappingEntity, Entity, Override, Table, Override

### Community 113 - "CustomStatisticRepository"
Cohesion: 0.13
Nodes (6): StatisticOperationType, add, remove, CustomStatisticRepository, CustomStatisticRepositoryImpl, Override

### Community 114 - "TagReplaceServiceImpl"
Cohesion: 0.15
Nodes (4): DishSlotRepository, ProposalSlotRepository, Override, TagReplaceServiceImpl

### Community 115 - "SecurityConfig.java"
Cohesion: 0.15
Nodes (14): Override, UserDetailsServiceImpl, AuthenticationRestController, SuppressWarnings, SecurityConfig, org.springframework.security.authentication.AuthenticationManager, org.springframework.security.authentication.AuthenticationProvider, org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration (+6 more)

### Community 116 - "BasicAmount"
Cohesion: 0.15
Nodes (4): BasicAmount, Override, EntityConvertibleAmount, Override

### Community 119 - "MergeResult"
Cohesion: 0.13
Nodes (5): MergeConflicts, MergeResult, Override, MergeResultResource, MergeResult

### Community 120 - "Dish"
Cohesion: 0.10
Nodes (3): Dish, Override, SuppressWarnings

### Community 121 - "Statistic"
Cohesion: 0.11
Nodes (3): Statistic, Override, StatisticListResource

### Community 122 - "ListTagStatistic"
Cohesion: 0.10
Nodes (5): Entity, Table, ListTagStatistic, Override, ResponseEntity

### Community 123 - "PostSearchTags"
Cohesion: 0.13
Nodes (3): Override, PostSearchTags, TagType

### Community 125 - "TargetResource"
Cohesion: 0.15
Nodes (5): EmbeddedTargetListResource, Override, TargetListResource, Override, TargetResource

### Community 126 - "FlatStringUtils"
Cohesion: 0.17
Nodes (6): FlatStringUtils, Override, StartupApplicationListener, TagReplaceService, org.springframework.context.ApplicationListener, org.springframework.context.event.ContextRefreshedEvent

### Community 129 - "JwtServiceImpl"
Cohesion: 0.22
Nodes (4): io.jsonwebtoken.Claims, javax.crypto.SecretKey, Override, JwtServiceImpl

### Community 130 - "NestedDish"
Cohesion: 0.13
Nodes (4): DishList, Override, Override, NestedDish

### Community 132 - "freemarker.template.Configuration"
Cohesion: 0.19
Nodes (5): freemarker.template.Configuration, AbstractContentBuilder, ContentBuilder, ContentBuilderFactory, SimpleContentBuilder

### Community 134 - "ListTagStatisticServiceImpl"
Cohesion: 0.20
Nodes (3): ListTagStatisticRepository, Override, ListTagStatisticServiceImpl

### Community 135 - "MailService"
Cohesion: 0.26
Nodes (10): BetaTestUserPropertyListener, BetaTestInformationSendTest, ResetPasswordSendTest, org.springframework.test.context.ContextConfiguration, org.springframework.test.context.TestPropertySource, EmailType, BetaNotification, BetaTestInformation (+2 more)

### Community 136 - "ProposalProcessor"
Cohesion: 0.17
Nodes (8): Override, ProposalProcessorFactoryImpl, ProposalProcessor, ProposalProcessorFactory, ProposalSearchType, FillInSearch, NewSearch, RefreshSearch

### Community 137 - "ListOperationType"
Cohesion: 0.13
Nodes (11): ListOperationType, DISH_ADD, DISH_REMOVE, LIST_ADD, LIST_REMOVE, NONE, STARTERLIST_ADD, STARTERLIST_REMOVE (+3 more)

### Community 138 - "Ingredient"
Cohesion: 0.14
Nodes (4): Ingredient, Override, IngredientList, Override

### Community 139 - "NestedShoppingList"
Cohesion: 0.14
Nodes (3): NestedShoppingList, Override, ShoppingListList

### Community 140 - "CustomUserRepositoryImpl"
Cohesion: 0.15
Nodes (4): CustomUserRepository, CustomUserRepositoryImpl, NamedParameterJdbcTemplate, Override

### Community 143 - "DishDTO"
Cohesion: 0.19
Nodes (3): DishDTO, Override, RatingInfoDTO

### Community 144 - "StatisticListPost"
Cohesion: 0.15
Nodes (7): CrossOrigin, GetMapping, PostMapping, RequestMapping, RestController, StatisticRestControllerApi, StatisticListPost

### Community 146 - "Initializer"
Cohesion: 0.23
Nodes (7): Initializer, Override, UITestContextInitializer, UITestPostgresqlContainer, org.springframework.context.ApplicationContextInitializer, org.springframework.context.ConfigurableApplicationContext, org.testcontainers.containers.PostgreSQLContainer

### Community 147 - "RoundingType"
Cohesion: 0.17
Nodes (7): RoundingType, EIGHTH, HUNDREDTH, QUARTER, THIRD, THOUSANDTH, UNIT

### Community 148 - "TagConverterProcessor"
Cohesion: 0.24
Nodes (4): AbstractConverterProcessor, Override, TagConverterProcessor, TagHandler

### Community 154 - "DomainConverterProcessor"
Cohesion: 0.33
Nodes (3): DeltaSpec, DomainConversionHandler, DomainConverterProcessor

### Community 157 - "ContentMap"
Cohesion: 0.28
Nodes (4): AbstractMap, Entry, ContentMap, Override

### Community 158 - "RatingUpdateInfoResource"
Cohesion: 0.31
Nodes (3): Override, RatingUpdateInfoResource, net.minidev.json.annotate.JsonIgnore

### Community 161 - "com.meg.listshop:listshop-service"
Cohesion: 0.50
Nodes (9): com.meg.listshop:conversion-api, com.meg.listshop:conversion-common, com.meg.listshop:conversion-data, com.meg.listshop:listshop-common, com.meg.listshop:listshop-common-data, com.meg.listshop:listshop-data, com.meg.listshop:listshop-service, com.meg.postoffice:postoffice-service (+1 more)

### Community 168 - "Application.java"
Cohesion: 0.29
Nodes (4): org.springframework.boot.autoconfigure.SpringBootApplication, org.springframework.boot.SpringApplication, org.springframework.scheduling.annotation.EnableScheduling, SpringApplication

### Community 169 - "CollectedItemStatus"
Cohesion: 0.33
Nodes (5): CollectedItemStatus, CROSSED_OFF, NEW, REMOVED, UPDATED

### Community 171 - ".createGroupSubquery"
Cohesion: 0.40
Nodes (4): CriteriaBuilder, CriteriaQuery, Root, Subquery

### Community 172 - "ListItemState"
Cohesion: 0.40
Nodes (4): ListItemState, ACTIVE, CROSSED_OFF, REMOVED

### Community 174 - "SharedPropertyKeys"
Cohesion: 0.67
Nodes (3): findByName(), SharedPropertyKeys, PREFERRED_DOMAIN

## Knowledge Gaps
- **157 isolated node(s):** `PreferredDomain`, `TestEmailSent`, `TestInfoRequested`, `add`, `remove` (+152 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 1513 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **48 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `TagEntity` connect `TagEntity` to `org.slf4j.Logger`, `UnitEntity`, `ListItemDetailEntity`, `org.springframework.test.context.ActiveProfiles`, `.toModel`, `.toModel`, `DishItemEntity`, `MealPlanEntity`, `CollectorContext`, `CollectedItem`, `DishDTO`, `.setItems`, `ListItemEntity`, `ProposalEntity`, `CustomListLayoutRepositoryImpl.java`, `ListLayoutCategoryEntity`, `ListLayoutEntity`, `TargetEntity`, `DishEntity`, `AdminTagFullInfo`, `.toEntity`, `SimpleListItemDTO`, `.getDishForUserById`, `.updateTag`, `ProposalSlotEntity`, `.testUpdateIngredientInDish_ConversionNewMarkers`, `TagType`, `.getId`, `DishItemDTO`, `ProposalGeneratorServiceImplTest`, `LayoutCategoryDTO`, `TagServiceImpl`, `FoodEntity`, `ListShopCategory`, `FoodCategoryMappingEntity`, `BasicAmount`, `FlatStringUtils`?**
  _High betweenness centrality (0.080) - this node is a cross-community bridge._
- **Why does `ListItemEntity` connect `ListItemEntity` to `org.slf4j.Logger`, `ShoppingListEntity`, `UnitEntity`, `ListItemDetailEntity`, `ListTagStatisticServiceImpl`, `org.springframework.test.context.ActiveProfiles`, `.toModel`, `.toModel`, `DishItemEntity`, `ListOperationType`, `CollectorContext`, `CollectedItem`, `TagEntity`, `FractionType`, `ListLayoutCategoryEntity`, `org.springframework.data.jpa.repository.Query`, `.findWithDetailsByListId`, `.toEntity`, `ShoppingListServiceImpl`, `Category`, `TagType`, `ShoppingListProperties`, `Statistic`, `FlatStringUtils`?**
  _High betweenness centrality (0.074) - this node is a cross-community bridge._
- **Why does `UnitEntity` connect `UnitEntity` to `org.slf4j.Logger`, `ListItemDetailEntity`, `org.springframework.test.context.ActiveProfiles`, `DishItemEntity`, `FactorCriteria`, `.getUnit`, `ListItemEntity`, `ConvertibleAmount`, `FractionType`, `ProcessingContext`, `org.springframework.data.jpa.repository.Query`, `UnitType`, `ConversionFactor`, `.updateTag`, `FoodConversionEntity`, `ConversionFactorEntity`, `org.springframework.stereotype.Component`, `ConversionBridgeFactorEntity`, `.makeUSUnit`, `BasicAmount`?**
  _High betweenness centrality (0.055) - this node is a cross-community bridge._
- **What connects `PreferredDomain`, `TestEmailSent`, `TestInfoRequested` to the rest of the system?**
  _157 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `org.slf4j.Logger` be split into smaller, more focused modules?**
  _Cohesion score 0.03214285714285714 - nodes in this community are weakly interconnected._
- **Should `com.fasterxml.jackson.annotation.JsonIgnore` be split into smaller, more focused modules?**
  _Cohesion score 0.024521072796934867 - nodes in this community are weakly interconnected._
- **Should `ShoppingListEntity` be split into smaller, more focused modules?**
  _Cohesion score 0.03812260536398467 - nodes in this community are weakly interconnected._