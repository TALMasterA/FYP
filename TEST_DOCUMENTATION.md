# Test Suite Documentation

## Overview
Comprehensive test suite for the FYP Android application with 171+ test methods covering critical business logic, data models, and domain rules.

## Test Structure

### 1. Data Layer Tests (app/src/test/java/com/example/fyp/data/)

#### QuizParserTest.kt (9 tests)
Tests JSON quiz parsing logic:
- ✅ Parse simple JSON quiz with questions array
- ✅ Parse JSON array format
- ✅ Handle invalid JSON gracefully
- ✅ Handle markdown code blocks (```json ... ```)
- ✅ Skip questions with invalid correctIndex
- ✅ Parse 10 questions correctly
- ✅ Skip questions with blank fields
- ✅ Skip questions with wrong number of options
- **Issue Fixed**: Updated from text format to JSON format parsing

#### ContentCleanerTest.kt (2 tests)
Tests quiz section extraction:
- ✅ Extract quiz section with colon header
- ✅ Extract quiz section without colon header

#### CoinEligibilityTest.kt (18 tests)
Tests anti-cheat coin eligibility logic:
- ✅ Score must be > 0
- ✅ Generated history count must match current count
- ✅ Minimum 10 records increment for re-earning
- ✅ First quiz always eligible
- ✅ Edge cases and boundary conditions
- ✅ Scenario tests for typical workflows

### 2. Domain Layer Tests (app/src/test/java/com/example/fyp/domain/)

#### GenerationEligibilityTest.kt (20 tests)
Tests material generation anti-cheat rules:
- ✅ Word bank regeneration (20 records minimum)
- ✅ Quiz regeneration (version mismatch)
- ✅ Learning sheet regeneration (5 records minimum)
- ✅ Edge cases at boundaries
- ✅ Scenario tests for user workflows

#### UnlockColorPaletteWithCoinsUseCaseTest.kt (11 tests)
Tests coin-based unlock system:
- ✅ Free palettes unlock without deduction
- ✅ Paid palettes deduct coins correctly
- ✅ Insufficient coins return error
- ✅ Transaction safety (deduct then unlock)
- ✅ Multiple unlock scenarios
- **Uses Mockito** for repository mocking

#### LanguageValidationTest.kt (14 tests)
Tests language code validation:
- ✅ Valid locale format (en-US, es-ES, etc.)
- ✅ 16 supported languages validated
- ✅ Language pair validation
- ✅ Code normalization
- ✅ Asian and European language groups

### 3. Model Layer Tests (app/src/test/java/com/example/fyp/model/)

#### QuizModelTest.kt (20 tests)
Tests quiz data models:
- ✅ QuizQuestion validation (4 options, correct index)
- ✅ QuizAnswer correctness tracking
- ✅ QuizAttempt scoring calculations
- ✅ Percentage computation (perfect, partial, zero)
- ✅ QuizStats tracking
- ✅ UserCoinStats management

#### TranslationRecordTest.kt (22 tests)
Tests translation record model:
- ✅ Source and target text storage
- ✅ Session grouping (sessionId)
- ✅ Speaker tracking (A/B for continuous mode)
- ✅ Direction tracking (A_to_B, B_to_A)
- ✅ Sequence ordering within sessions
- ✅ Mode validation (speech, continuous)
- ✅ Bidirectional conversation scenarios

#### FavoriteRecordTest.kt (21 tests)
Tests favorite/bookmark model:
- ✅ Creation from translation records
- ✅ Note management (empty, multi-line)
- ✅ Timestamp tracking
- ✅ User ownership
- ✅ Language pair support
- ✅ Sorting by creation time
- ✅ Vocabulary collection scenarios

#### HistorySessionTest.kt (21 tests)
Tests session management model:
- ✅ Session identification and naming
- ✅ Session renaming workflow
- ✅ Update timestamp tracking
- ✅ Session uniqueness (by ID)
- ✅ Sorting by update time
- ✅ Session organization by topic/date
- ✅ Unicode and special characters in names

#### UserSettingsTest.kt (27 tests)
Tests user settings validation:
- ✅ Default values
- ✅ Language code format
- ✅ Font size scale (0.8x - 2.0x)
- ✅ Theme mode (system, light, dark)
- ✅ Color palette unlocking
- ✅ Voice settings per language
- ✅ History limit expansion (50-150)
- ✅ Multi-language user scenarios

## Test Coverage Summary

| Component | Test File | Tests | Coverage |
|-----------|-----------|-------|----------|
| Quiz Parsing | QuizParserTest | 9 | JSON parsing, validation |
| Content Cleaning | ContentCleanerTest | 2 | Section extraction |
| Coin Eligibility | CoinEligibilityTest | 18 | Anti-cheat rules |
| Generation Eligibility | GenerationEligibilityTest | 20 | Regeneration rules |
| Coin Unlock | UnlockColorPaletteWithCoinsUseCaseTest | 11 | Transaction logic |
| Language Validation | LanguageValidationTest | 14 | Code format, pairs |
| Quiz Models | QuizModelTest | 20 | Scoring, stats |
| Translation Records | TranslationRecordTest | 22 | Sessions, speakers |
| Favorites | FavoriteRecordTest | 21 | Bookmarks, notes |
| History Sessions | HistorySessionTest | 21 | Session management |
| User Settings | UserSettingsTest | 27 | Preferences, limits |
| **TOTAL** | **11 files** | **171+** | **Comprehensive** |

## Testing Frameworks Used

- **JUnit 4** (junit:4.13.2) - Test framework
- **Mockito** (5.7.0) - Mocking framework for use cases
- **Mockito-Kotlin** (5.1.0) - Kotlin DSL for Mockito
- **Kotlinx Coroutines Test** (1.8.1) - Testing coroutines

## Key Business Logic Tested

### Anti-Cheat Systems
1. **Coin Eligibility** - Prevents farming coins by requiring 10+ new records between awards
2. **Word Bank Regeneration** - Requires 20+ new records to prevent spam regeneration
3. **Quiz Regeneration** - Requires version mismatch (new learning material)
4. **Learning Sheet Regeneration** - Requires 5+ new records

### Data Validation
1. **Language Codes** - 16 supported languages with proper locale format
2. **Quiz Questions** - Must have exactly 4 options, valid correct index (0-3)
3. **User Settings** - Theme modes, font scales, history limits all validated
4. **Translation Records** - Session grouping, speaker tracking for conversations

### Scoring & Stats
1. **Quiz Scoring** - Percentage calculation from correct answers
2. **Coin Tracking** - Total and per-language breakdown
3. **Quiz Stats** - Attempt count, averages, high/low scores

## Test Quality Features

✅ **Comprehensive Coverage** - 171+ tests covering all critical business logic  
✅ **Edge Cases** - Boundary conditions, empty values, null handling  
✅ **Scenario Tests** - Real-world user workflows  
✅ **Clear Naming** - Descriptive test names using backticks  
✅ **Proper Assertions** - Using assertEquals, assertTrue, assertFalse with clear messages  
✅ **Mock Usage** - Proper mocking for external dependencies  
✅ **Coroutine Support** - runBlocking for suspend functions  

## Known Issues

⚠️ **Build System** - Cannot compile tests due to network connectivity issues preventing AGP download  
⚠️ **Integration Tests** - No integration tests yet (only unit tests)  
⚠️ **UI Tests** - No Compose UI tests yet  

## How to Run Tests (When Build is Fixed)

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests QuizParserTest

# Run tests with coverage
./gradlew testDebugUnitTest jacocoTestReport
```

## Test Maintenance

- All tests follow consistent naming: `ComponentName + Test.kt`
- Test methods use descriptive backtick names
- Tests are organized by layer (data, domain, model)
- Each test file has a header comment describing what it tests
- Tests use AAA pattern: Arrange, Act, Assert

## Future Improvements

1. Add integration tests for repository operations
2. Add Compose UI tests for screens
3. Add performance tests for large datasets
4. Add tests for ViewModels with state management
5. Increase code coverage to 80%+ (currently focused on critical logic)
