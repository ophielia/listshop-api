# Graph Report - listshop-api  (2026-11-15)

## Corpus Check
- 238 files · ~949,089 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 7899 nodes · 23258 edges · 250 communities (114 shown, 136 thin omitted)
- Extraction: 84% EXTRACTED · 16% INFERRED · 0% AMBIGUOUS · INFERRED: 3767 edges (avg confidence: 0.81)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- Unit and Conversion Entities
- Transactional Entities and Specifications
- Exception Handling and Repositories
- Meal Plan Proposal Logic
- List Layout and Tags
- Target Proposals and Slots
- Unit Repositories and Rounding
- API Models and Rest Controllers
- Data Inflation and Flattening
- Rounding Utilities and DTOs
- Community 10
- Community 11
- Community 12
- Community 13
- Community 14
- Community 15
- Community 16
- Community 17
- Community 18
- Community 19
- Community 20
- Community 21
- Community 22
- Community 23
- Community 24
- Community 25
- Community 26
- Community 27
- Community 28
- Community 29
- Community 30
- Community 31
- Community 32
- Community 33
- Community 34
- Community 35
- Community 36
- Community 37
- Community 38
- Community 39
- Community 40
- Community 41
- Community 42
- Community 43
- Community 44
- Community 45
- Community 46
- Community 47
- Community 48
- Community 49
- Community 50
- Community 51
- Community 52
- Community 53
- Community 54
- Community 55
- Community 56
- Community 57
- Community 58
- Community 59
- Community 60
- Community 61
- Community 63
- Community 64
- Community 65
- Community 66
- Community 67
- Community 68
- Community 69
- Community 70
- Community 71
- Community 72
- Community 73
- Community 74
- Community 75
- Community 76
- Community 77
- Community 78
- Community 79
- Community 80
- Community 81
- Community 82
- Community 83
- Community 84
- Community 85
- Community 86
- Community 87
- Community 88
- Community 89
- Community 90
- Community 91
- Community 92
- Community 93
- Community 94
- Community 95
- Community 96
- Community 97
- Community 98
- Community 99
- Community 100
- Community 101
- Community 102
- Community 103
- Community 104
- Community 105
- Community 106
- Community 107
- Community 108
- Community 109
- Community 110
- Community 111
- Community 112
- Community 113
- Community 114
- Community 115
- Community 116
- Community 117
- Community 118
- Community 119
- Community 120
- Community 121
- Community 122
- Community 123
- Community 124
- Community 125
- Community 126
- Community 127
- Community 128
- Community 129
- Community 130
- Community 131
- Community 132
- Community 133
- Community 134
- Community 135
- Community 136
- Community 137
- Community 138
- Community 139
- Community 140
- Community 141
- Community 142
- Community 143
- Community 144
- Community 145
- Community 146
- Community 147
- Community 148
- Community 149
- Community 150
- Community 151
- Community 153
- Community 154
- Community 155
- Community 156
- Community 157
- Community 158
- Community 159
- Community 160
- Community 161
- Community 162
- Community 163
- Community 164
- Community 165
- Community 166
- Community 167
- Community 168
- Community 169
- Community 170
- Community 171
- Community 172
- Community 173
- Community 174
- Community 175
- Community 176
- Community 177
- Community 178
- Community 179
- Community 180
- Community 182
- Community 183
- Community 184
- Community 185
- Community 188
- Community 189
- Community 190
- Community 191
- Community 192
- Community 193
- Community 195
- Community 196
- Community 197
- Community 198
- Community 199
- Community 200
- Community 201
- Community 202
- Community 203
- Community 204
- Community 206
- Community 207
- Community 208
- Community 209
- Community 210
- Community 211
- Community 212
- Community 213
- Community 214
- Community 215
- Community 216
- Community 217
- Community 219
- Community 220
- Community 222
- Community 223
- Community 224
- Community 225
- Community 226
- Community 227
- Community 229
- Community 230
- Community 231
- Community 232
- Community 234
- Community 235
- Community 238
- Community 239
- Community 240
- Community 242
- Community 243
- Community 244
- Community 245
- Community 246
- Community 247
- Community 248
- Community 249

## God Nodes (most connected - your core abstractions)
1. `TagEntity` - 238 edges
2. `ListItemEntity` - 210 edges
3. `UnitEntity` - 154 edges
4. `UserEntity` - 110 edges
5. `ConvertibleAmount` - 103 edges
6. `UserService` - 103 edges
7. `ListItemDetailEntity` - 99 edges
8. `ProcessingContext` - 98 edges
9. `TagService` - 97 edges
10. `ListShopPostgresqlContainer` - 94 edges

## Surprising Connections (you probably didn't know these)
- `LegacyShoppingListServiceImpl` --references--> `ItemProcessingException`  [EXTRACTED]
  listshop-lmt/listshop-service/src/main/java/com/meg/listshop/lmt/list/impl/LegacyShoppingListServiceImpl.java → listshop-api/src/main/java/com/meg/listshop/lmt/api/exception/ItemProcessingException.java
- `AdminLayoutRestController` --implements--> `AdminLayoutRestControllerApi`  [EXTRACTED]
  listshop-lmt/listshop-service/src/main/java/com/meg/listshop/admin/controller/AdminLayoutRestController.java → listshop-api/src/main/java/com/meg/listshop/admin/controller/AdminLayoutRestControllerApi.java
- `AdminUserRestController` --implements--> `AdminUserRestControllerApi`  [EXTRACTED]
  listshop-lmt/listshop-service/src/main/java/com/meg/listshop/admin/controller/AdminUserRestController.java → listshop-api/src/main/java/com/meg/listshop/admin/controller/AdminUserRestControllerApi.java
- `LayoutRestController` --implements--> `LayoutRestControllerApi`  [EXTRACTED]
  listshop-lmt/listshop-service/src/main/java/com/meg/listshop/lmt/api/web/controller/LayoutRestController.java → listshop-api/src/main/java/com/meg/listshop/lmt/api/controller/LayoutRestControllerApi.java
- `TagRestController` --implements--> `TagRestControllerApi`  [EXTRACTED]
  listshop-lmt/listshop-service/src/main/java/com/meg/listshop/lmt/api/web/controller/TagRestController.java → listshop-api/src/main/java/com/meg/listshop/lmt/api/controller/TagRestControllerApi.java

## Import Cycles
- None detected.

## Communities (250 total, 136 thin omitted)

### Community 0 - "Unit and Conversion Entities"
Cohesion: 0.03
Nodes (29): UnitEntity, UnitSubtype, LIQUID, NONE, SOLID, VOLUME, WEIGHT, findByName() (+21 more)

### Community 1 - "Transactional Entities and Specifications"
Cohesion: 0.04
Nodes (22): SpecificationType, ALL, MIXED, NONE, ListItemEntity, ListConversionService, AbstractTransition, ProcessingType (+14 more)

### Community 2 - "Exception Handling and Repositories"
Cohesion: 0.05
Nodes (32): ActionInvalidException, ObjectNotFoundException, ObjectNotYoursException, AmountRepository, ShadowTagRepository, AdminUserRestController, BetaTestUserPropertyListener, JwtService (+24 more)

### Community 3 - "Meal Plan Proposal Logic"
Cohesion: 0.05
Nodes (9): ProposalContextEntity, ProposalEntity, ProposalContextRepository, ProposalRepository, ProposalGeneratorServiceImpl, ProposalServiceImpl, ProposalGeneratorService, ProposalService (+1 more)

### Community 4 - "List Layout and Tags"
Cohesion: 0.03
Nodes (7): TagEntity, ListSearchServiceImpl, ListSearchService, TagChangeToCategoryListener, TagChangeListener, TagStructureService, TagTestBuilder

### Community 5 - "Target Proposals and Slots"
Cohesion: 0.03
Nodes (5): TargetProposal, TargetProposalSlot, Tag, Target, TargetSlot

### Community 6 - "Unit Repositories and Rounding"
Cohesion: 0.08
Nodes (22): UnitRepository, RoundingUtils, ConversionRequest, ConversionTargetType, Dish, List, ConversionAddException, ConversionFactorException (+14 more)

### Community 7 - "API Models and Rest Controllers"
Cohesion: 0.12
Nodes (37): Application, AdminUserRestControllerTest, ListShopPostgresqlContainer, StatisticRestControllerTest, V2LayoutRestControllerTest, ListItemRepositoryTest, DishSearchServiceImplTest, MergeItemCollectorTest (+29 more)

### Community 8 - "Data Inflation and Flattening"
Cohesion: 0.05
Nodes (7): AbstractInflateAndFlatten, TargetEntity, TargetSlotEntity, TargetRepository, TargetSlotRepository, TargetServiceImpl, TargetService

### Community 9 - "Rounding Utilities and DTOs"
Cohesion: 0.04
Nodes (4): RoundingUtilsTest, LegacyMealPlanRestControllerTest, NewMealPlanRestControllerTest, DishServiceImplMockTest

### Community 10 - "Community 10"
Cohesion: 0.03
Nodes (8): LayoutRestControllerApi, TagRestControllerApi, V2LayoutRestControllerApi, V2TagRestControllerApi, TagPut, Tag, TagList, TagRestController

### Community 11 - "Community 11"
Cohesion: 0.05
Nodes (11): ItemOperationType, Copy, CrossOff, Move, Remove, RemoveAll, RemoveCrossedOff, UnCrossOff (+3 more)

### Community 13 - "Community 13"
Cohesion: 0.05
Nodes (3): ProposalRestControllerApi, LegacyShoppingListRestControllerApi, V2ShoppingListRestControllerApi

### Community 14 - "Community 14"
Cohesion: 0.14
Nodes (3): StateMachineActiveTransitionTest, RemovalTestsNoAmounts, RemovalTestsWithAmounts

### Community 15 - "Community 15"
Cohesion: 0.04
Nodes (6): AdminFoodRestController, CustomUserDetails, TagRestController, TagService, CountResult, TagServiceImplMockTest

### Community 16 - "Community 16"
Cohesion: 0.07
Nodes (7): MealPlanEntity, SlotEntity, MealPlanRepository, SlotRepository, MealPlanServiceImpl, MealPlanService, MealPlanServiceImplMockTest

### Community 17 - "Community 17"
Cohesion: 0.05
Nodes (13): UserCreateException, ActionIgnoredException, AuthenticationException, BadParameterException, ItemProcessingException, ProcessingException, ProposalProcessingException, TagStructureException (+5 more)

### Community 19 - "Community 19"
Cohesion: 0.05
Nodes (7): ListLayoutEntity, CustomListLayoutRepository, CustomListLayoutRepositoryImpl, ListLayoutRepository, BaseLayoutServiceImpl, LegacyLayoutServiceImpl, LayoutServiceImplMockTest

### Community 20 - "Community 20"
Cohesion: 0.05
Nodes (11): TargetType, PickUp, Standard, DishSearchService, DishTagSearchResult, FillInProposalProcessorImpl, NewRawSlotResult, AbstractProposalProcessor (+3 more)

### Community 22 - "Community 22"
Cohesion: 0.05
Nodes (5): ItemOperationPut, ListAddProperties, ShoppingListResource, LegacyShoppingListRestController, LegacyShoppingListService

### Community 24 - "Community 24"
Cohesion: 0.06
Nodes (5): AdminLayoutRestControllerApi, AuthenticationRestControllerApi, StatisticRestControllerApi, TargetRestControllerApi, TargetRestController

### Community 25 - "Community 25"
Cohesion: 0.07
Nodes (3): FactorCriteria, CacheKey, CustomConversionFactorRepositoryImpl

### Community 26 - "Community 26"
Cohesion: 0.07
Nodes (6): AdminFoodRestControllerTest, AdminTagRestControllerTest, AuthenticationRestControllerTest, CampaignRestControllerTest, ProposalRestControllerTest, TargetRestControllerTest

### Community 29 - "Community 29"
Cohesion: 0.08
Nodes (5): DeleteListTests, MergeTests, RetrieveListTests, ShoppingListRestControllerTest, TagTests

### Community 30 - "Community 30"
Cohesion: 0.05
Nodes (4): Amount, IngredientPut, PostListItem, V2DishRestControllerTest

### Community 31 - "Community 31"
Cohesion: 0.09
Nodes (4): ListItemDetailEntity, AmountTextBuilder, QuantityElements, RemoveLinkTransition

### Community 32 - "Community 32"
Cohesion: 0.09
Nodes (4): ProcessingContext, ProcessingUtils, ScalingProcessor, ScaleHandler

### Community 33 - "Community 33"
Cohesion: 0.07
Nodes (6): UserEntity, AuthorityRepository, UserRepository, UserDetailsServiceImpl, UserServiceImpl, SecurityConfig

### Community 34 - "Community 34"
Cohesion: 0.08
Nodes (3): UserPropertyEntity, UserPropertyServiceImpl, UserPropertyServiceImplMockTest

### Community 35 - "Community 35"
Cohesion: 0.06
Nodes (9): TagType, DishType, Ingredient, NonEdible, Rating, ICountResult, ItemToCategoryDTO, ItemRepository (+1 more)

### Community 36 - "Community 36"
Cohesion: 0.08
Nodes (6): CategoryTagMapping, LayoutCategoryDTO, LayoutDTO, ListLayoutCategoryRepository, V2LayoutServiceImpl, V2LayoutServiceImplMockTest

### Community 37 - "Community 37"
Cohesion: 0.08
Nodes (9): ConversionFactorRepository, FactorCriteriaBuilder, ConversionTarget, BaseScaleHandler, ContextScaler, GenericScaler, SingleUnitScaler, TagUnitScaler (+1 more)

### Community 39 - "Community 39"
Cohesion: 0.05
Nodes (5): DishRatingInfo, RatingInfo, RatingUpdateInfo, DishRating, DishRatings

### Community 40 - "Community 40"
Cohesion: 0.09
Nodes (6): ConversionFactor, SimpleConversionFactor, CustomConversionFactorRepository, FactorProvider, TagContextScaler, HybridTagHandler

### Community 42 - "Community 42"
Cohesion: 0.06
Nodes (8): PostUserProperties, ProposalResource, UserPropertiesResource, UserProperty, UserPropertyResource, AbstractListShopResource, ListShopModel, ListShopResource

### Community 43 - "Community 43"
Cohesion: 0.06
Nodes (4): DishItemEntity, DishItemRepository, BasicAmount, ServiceTestUtils

### Community 45 - "Community 45"
Cohesion: 0.06
Nodes (3): PostSearchUsers, AdminLayoutRestController, ModelMapper

### Community 46 - "Community 46"
Cohesion: 0.04
Nodes (48): affinity, autoscaling, enabled, maxReplicas, minReplicas, targetCPUUtilizationPercentage, url, fullnameOverride (+40 more)

### Community 48 - "Community 48"
Cohesion: 0.07
Nodes (4): DishSlotEntity, ProposalSlotEntity, DishSlotRepository, ProposalSlotRepository

### Community 49 - "Community 49"
Cohesion: 0.08
Nodes (5): ShadowTags, AutoTagSubject, AutoTagProcessor, Type, AbstractAutoTagProcessor

### Community 50 - "Community 50"
Cohesion: 0.06
Nodes (7): TagInfoDTO, CustomTagInfoRepository, TagInfoMapper, TagInfoRepositoryImpl, DishRatingBuilder, RatingStructure, RatingStructureTree

### Community 51 - "Community 51"
Cohesion: 0.09
Nodes (3): AbstractItemCollector, CollectedItem, MergeItemCollector

### Community 52 - "Community 52"
Cohesion: 0.19
Nodes (4): ProposalRequest, ProposalRequestBuilder, FillInProposalProcessorImplTest, ProcessorTestUtils

### Community 55 - "Community 55"
Cohesion: 0.07
Nodes (11): DishSort, DishSortDirection, ASC, DESC, DishSortKey, CreatedOn, LastUsed, Name (+3 more)

### Community 56 - "Community 56"
Cohesion: 0.06
Nodes (7): ModifierType, Marker, Unit, UnitSize, ModifierMappingEntity, SuggestionDTO, CustomAmountRepository

### Community 57 - "Community 57"
Cohesion: 0.10
Nodes (7): CustomAmountRepositoryImpl, CustomFoodMappingRepositoryImpl, FoodMappingMapper, CustomTagRepositoryImpl, TagConflictMapper, ItemMappingDTOMapper, ListMappingCustomRepositoryImpl

### Community 58 - "Community 58"
Cohesion: 0.08
Nodes (5): ClientType, Mobile, Web, UserDeviceEntity, UserServiceImplMockTest

### Community 59 - "Community 59"
Cohesion: 0.09
Nodes (3): ListShopPayload, PostToken, UserRestControllerTest

### Community 60 - "Community 60"
Cohesion: 0.06
Nodes (3): DishItemDTO, CustomDishItemRepository, CustomDishItemRepositoryImpl

### Community 61 - "Community 61"
Cohesion: 0.08
Nodes (4): EmbeddedListLayoutListResource, ListLayoutListResource, ListLayoutResource, JwtUser

### Community 63 - "Community 63"
Cohesion: 0.07
Nodes (8): ApproachType, REV_SORTED_WHEEL, SORTED_WHEEL, WHEEL, WHEEL_MIXED, ContextApproachEntity, ProcessResult, TargetProposalSettings

### Community 64 - "Community 64"
Cohesion: 0.07
Nodes (18): TagFilterType, All, GroupsOnly, NoGroups, ParentTags, ToReview, IncludeType, EXCLUDE (+10 more)

### Community 65 - "Community 65"
Cohesion: 0.10
Nodes (3): TagRelationEntity, TagRelationRepository, TagStructureServiceImpl

### Community 67 - "Community 67"
Cohesion: 0.07
Nodes (16): ListOperationType, DISH_ADD, DISH_REMOVE, LIST_ADD, LIST_REMOVE, NONE, STARTERLIST_ADD, STARTERLIST_REMOVE (+8 more)

### Community 68 - "Community 68"
Cohesion: 0.08
Nodes (4): DishEntity, DishRepository, DishMapper, DishTestBuilder

### Community 70 - "Community 70"
Cohesion: 0.10
Nodes (7): TagInstructionEntity, TextInstructionEntity, Instruction, TagInstructionRepository, TextInstructionRepository, TextProcessorImpl, TagProcessorImpl

### Community 71 - "Community 71"
Cohesion: 0.09
Nodes (3): ClientVersions, PostChangePassword, ProposalRestController

### Community 72 - "Community 72"
Cohesion: 0.13
Nodes (5): CollectedItemStatus, CROSSED_OFF, NEW, REMOVED, UPDATED

### Community 73 - "Community 73"
Cohesion: 0.08
Nodes (4): Category, CategoryListResource, CategoryResource, EmbeddedListCategoryListResource

### Community 74 - "Community 74"
Cohesion: 0.07
Nodes (3): ListLayout, ListLayoutCategory, ListLayoutList

### Community 76 - "Community 76"
Cohesion: 0.07
Nodes (3): LegendSource, ShoppingList, ShoppingListCategory

### Community 77 - "Community 77"
Cohesion: 0.07
Nodes (4): FoodCategoryMappingEntity, FoodMappingDTO, CustomFoodMappingRepository, FoodCategoryMappingRepository

### Community 79 - "Community 79"
Cohesion: 0.09
Nodes (3): AbstractCategory, ListLayoutCategoryPojo, ListShopCategory

### Community 81 - "Community 81"
Cohesion: 0.08
Nodes (4): CategoryMappingListResource, EmbeddedCategoryMappingListResource, FoodCategoryMapping, FoodCategoryMappingResource

### Community 84 - "Community 84"
Cohesion: 0.07
Nodes (3): Item, MergeRequest, ItemCategoryPojo

### Community 85 - "Community 85"
Cohesion: 0.08
Nodes (3): Statistic, StatisticListPost, StatisticListResource

### Community 86 - "Community 86"
Cohesion: 0.14
Nodes (5): TokenType, PasswordReset, TokenEntity, TokenRepository, TokenServiceImplTest

### Community 87 - "Community 87"
Cohesion: 0.08
Nodes (3): AdminUserRestControllerApi, AdminUser, AdminUserListResource

### Community 88 - "Community 88"
Cohesion: 0.09
Nodes (7): CategoryType, Frequent, Highlight, HighlightList, Standard, UnCategorized, ShoppingListProperties

### Community 94 - "Community 94"
Cohesion: 0.14
Nodes (3): CollectorContextBuilder, ListItemCollectorTest, ListTagStatisticServiceImplTest

### Community 96 - "Community 96"
Cohesion: 0.10
Nodes (3): DateUtils, FlatStringUtils, TargetServiceConstants

### Community 100 - "Community 100"
Cohesion: 0.10
Nodes (4): EmbeddedFoodCategoryListResource, FoodCategory, FoodCategoryListResource, FoodCategoryResource

### Community 101 - "Community 101"
Cohesion: 0.10
Nodes (4): EmbeddedFoodListResource, Food, FoodListResource, FoodResource

### Community 102 - "Community 102"
Cohesion: 0.10
Nodes (4): EmbeddedLayoutCategoryListResource, LayoutCategory, LayoutCategoryListResource, LayoutCategoryResource

### Community 104 - "Community 104"
Cohesion: 0.10
Nodes (3): ListTagStatistic, ListTagStatisticRepository, ListTagStatisticServiceImpl

### Community 105 - "Community 105"
Cohesion: 0.15
Nodes (3): PostofficeConfiguration, MailPropertiesTestConfiguration, PostofficeTestConfiguration

### Community 106 - "Community 106"
Cohesion: 0.07
Nodes (27): runs-on, steps, description, required, GITHUB_TOKEN, buildVersion, jobs, build (+19 more)

### Community 107 - "Community 107"
Cohesion: 0.08
Nodes (7): TargetProposalDish, Dish, ItemSourceType, Dish, List, Special, UserAccount

### Community 109 - "Community 109"
Cohesion: 0.10
Nodes (4): AdminUserDetailsEntity, AdminUserDetailsRepository, UserDeviceRepository, ExpiredTokenCleanupTask

### Community 110 - "Community 110"
Cohesion: 0.11
Nodes (11): ContextType, List, Merge, NonSpecified, StatisticCountType, Dish, List, None (+3 more)

### Community 111 - "Community 111"
Cohesion: 0.10
Nodes (17): doubleValueOf(), FractionType, FiveEighths, One, OneEighth, OneHalf, OneQuarter, OneThird (+9 more)

### Community 116 - "Community 116"
Cohesion: 0.08
Nodes (3): PutDish, DishDTO, RatingInfoDTO

### Community 120 - "Community 120"
Cohesion: 0.09
Nodes (5): LongTagIdPairDTO, CustomTagRelationRepository, CustomTagRepository, CustomTagRelationRepositoryImpl, LongTagIdPairMapper

### Community 121 - "Community 121"
Cohesion: 0.08
Nodes (24): runs-on, steps, GITHUB_TOKEN, jobs, build, name, on, release (+16 more)

### Community 130 - "Community 130"
Cohesion: 0.11
Nodes (3): EmbeddedSuggestionListResource, Suggestion, SuggestionListResource

### Community 134 - "Community 134"
Cohesion: 0.14
Nodes (4): AuthorityEntity, AuthorityName, ROLE_ADMIN, ROLE_USER

### Community 135 - "Community 135"
Cohesion: 0.12
Nodes (5): SpringDocConfiguration, StaticResourceConfiguration, TestConfiguration, WebConfiguration, ListLayoutProperties

### Community 139 - "Community 139"
Cohesion: 0.13
Nodes (3): EmbeddedMealPlanListResource, MealPlanListResource, MealPlanResource

### Community 140 - "Community 140"
Cohesion: 0.13
Nodes (3): ListItemRepositoryImpl, ListTagStatisticService, ItemCollector

### Community 142 - "Community 142"
Cohesion: 0.13
Nodes (6): StringTools, CampaignControllerApi, CampaignRestController, AmountRestController, CampaignService, AmountService

### Community 144 - "Community 144"
Cohesion: 0.13
Nodes (4): MergeConflicts, MergeResult, MergeResultResource, MergeResult

### Community 149 - "Community 149"
Cohesion: 0.15
Nodes (3): EmbeddedTagListResource, TagListResource, TagResource

### Community 150 - "Community 150"
Cohesion: 0.15
Nodes (3): EmbeddedTargetListResource, TargetListResource, TargetResource

### Community 156 - "Community 156"
Cohesion: 0.11
Nodes (18): runs-on, steps, default, description, required, defaults, run, containerVersion (+10 more)

### Community 158 - "Community 158"
Cohesion: 0.15
Nodes (3): DishListResource, DishResource, EmbeddedDishListResource

### Community 164 - "Community 164"
Cohesion: 0.19
Nodes (4): AbstractContentBuilder, ContentBuilder, ContentBuilderFactory, SimpleContentBuilder

### Community 165 - "Community 165"
Cohesion: 0.24
Nodes (3): JwtAuthFilter, JwtAuthFilterDummyImpl, JwtAuthFilterImpl

### Community 169 - "Community 169"
Cohesion: 0.17
Nodes (7): ProposalProcessorFactoryImpl, ProposalProcessor, ProposalProcessorFactory, ProposalSearchType, FillInSearch, NewSearch, RefreshSearch

### Community 172 - "Community 172"
Cohesion: 0.12
Nodes (15): runs-on, steps, jobs, build, name, on, push, branches (+7 more)

### Community 177 - "Community 177"
Cohesion: 0.16
Nodes (4): DomainConversionHandler, AbstractConverterProcessor, ConverterProcessor, DomainConverterProcessor

### Community 178 - "Community 178"
Cohesion: 0.15
Nodes (3): FoodCategoryEntity, FoodCategoryRepository, UserPropertyRepository

### Community 182 - "Community 182"
Cohesion: 0.13
Nodes (14): runs-on, steps, jobs, build, name, on, release, types (+6 more)

### Community 188 - "Community 188"
Cohesion: 0.14
Nodes (13): runs-on, steps, jobs, build, name, on, name, run (+5 more)

### Community 190 - "Community 190"
Cohesion: 0.23
Nodes (3): Initializer, UITestContextInitializer, UITestPostgresqlContainer

### Community 192 - "Community 192"
Cohesion: 0.19
Nodes (4): SourceReferenceType, DISH, LIST, SourceDTO

### Community 200 - "Community 200"
Cohesion: 0.20
Nodes (7): RoundingType, EIGHTH, HUNDREDTH, QUARTER, THIRD, THOUSANDTH, UNIT

### Community 201 - "Community 201"
Cohesion: 0.20
Nodes (9): TagOperationType, AssignFood, AssignFoodCategory, AssignToUser, CopyFoodFromTag, CopyToStandard, MarkAsNoFoodVerified, MarkAsReviewed (+1 more)

### Community 207 - "Community 207"
Cohesion: 0.49
Nodes (10): com.meg.listshop:conversion-api, com.meg.listshop:conversion-common, com.meg.listshop:conversion-data, com.meg.listshop:conversion-service, com.meg.listshop:listshop-api, com.meg.listshop:listshop-common, com.meg.listshop:listshop-common-data, com.meg.listshop:listshop-data (+2 more)

### Community 210 - "Community 210"
Cohesion: 0.25
Nodes (7): interval, updates, directory, package-ecosystem, schedule, target-branch, version

### Community 214 - "Community 214"
Cohesion: 0.29
Nodes (6): Agent Guidelines, Database Migration Guidelines, Flyway, PostgreSQL, Spring Boot, V2 Layout Services

### Community 223 - "Community 223"
Cohesion: 0.33
Nodes (3): MealPlanType, Manual, Targeted

### Community 226 - "Community 226"
Cohesion: 0.40
Nodes (4): ListItemState, ACTIVE, CROSSED_OFF, REMOVED

### Community 229 - "Community 229"
Cohesion: 0.67
Nodes (3): findByName(), SharedPropertyKeys, PREFERRED_DOMAIN

### Community 232 - "Community 232"
Cohesion: 0.50
Nodes (3): SortOrMoveDirection, DOWN, UP

## Knowledge Gaps
- **162 isolated node(s):** `PreferredDomain`, `TestEmailSent`, `TestInfoRequested`, `add`, `remove` (+157 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 2014 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **136 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `TagEntity` connect `List Layout and Tags` to `Transactional Entities and Specifications`, `Exception Handling and Repositories`, `Meal Plan Proposal Logic`, `Unit Repositories and Rounding`, `API Models and Rest Controllers`, `Data Inflation and Flattening`, `Community 140`, `Community 14`, `Community 15`, `Community 16`, `Community 18`, `Community 19`, `Community 27`, `Community 35`, `Community 36`, `Community 41`, `Community 43`, `Community 47`, `Community 48`, `Community 51`, `Community 53`, `Community 60`, `Community 65`, `Community 67`, `Community 68`, `Community 72`, `Community 79`, `Community 89`, `Community 96`, `Community 236`, `Community 110`, `Community 114`, `Community 120`?**
  _High betweenness centrality (0.093) - this node is a cross-community bridge._
- **What connects `PreferredDomain`, `TestEmailSent`, `TestInfoRequested` to the rest of the system?**
  _162 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Unit and Conversion Entities` be split into smaller, more focused modules?**
  _Cohesion score 0.027915071353985382 - nodes in this community are weakly interconnected._
- **Why does `ListItemEntity` connect `Transactional Entities and Specifications` to `Unit and Conversion Entities`, `Exception Handling and Repositories`, `List Layout and Tags`, `Unit Repositories and Rounding`, `API Models and Rest Controllers`, `Rounding Utilities and DTOs`, `Community 11`, `Community 140`, `Community 14`, `Community 17`, `Community 27`, `Community 31`, `Community 35`, `Community 168`, `Community 41`, `Community 51`, `Community 67`, `Community 68`, `Community 72`, `Community 84`, `Community 88`, `Community 89`, `Community 94`, `Community 96`, `Community 104`, `Community 108`, `Community 110`, `Community 111`?**
  _High betweenness centrality (0.046) - this node is a cross-community bridge._
- **Should `Transactional Entities and Specifications` be split into smaller, more focused modules?**
  _Cohesion score 0.04023129921259842 - nodes in this community are weakly interconnected._
- **Why does `TagInfoDTO` connect `Community 50` to `Community 64`, `Community 68`, `Community 39`, `Community 15`, `Community 57`?**
  _High betweenness centrality (0.036) - this node is a cross-community bridge._
- **Should `Exception Handling and Repositories` be split into smaller, more focused modules?**
  _Cohesion score 0.05330564209068882 - nodes in this community are weakly interconnected._