# Architecture Overview

## Design Patterns

### MVVM (Model-View-ViewModel)
- **Models**: Data classes representing domain objects
- **Views**: Compose UI screens that render state
- **ViewModels**: Manage UI state and handle business logic

### Repository Pattern
- Abstracts data sources (API + Database)
- Single source of truth for data operations
- Enables easy testing and switching implementations

### Dependency Injection
- Manual DI in ViewModels
- Could be enhanced with Hilt for scalability

## Layer Architecture

### Data Layer
```
data/
├── api/           # Fish Audio REST client
├── db/            # Room database entities & DAOs
└── repository/    # Data abstraction layer
```

**Responsibilities:**
- API communication with Fish Audio
- Local database persistence
- Data validation and error handling

### UI Layer
```
ui/
├── screens/       # Compose screen composables
├── components/    # Reusable UI components
└── theme/         # Color scheme, typography, shapes
```

**Responsibilities:**
- User interaction handling
- State rendering
- Navigation
- Theming

### ViewModel Layer
```
viewmodel/
├── SharedViewModel     # App-wide state
├── SettingsViewModel   # Settings state
└── (Others as needed)
```

**Responsibilities:**
- State management with StateFlow
- Business logic orchestration
- API/Database interaction coordination

### Utility Layer
```
util/
├── Constants.kt        # App-wide constants
└── PreferencesManager  # Encrypted SharedPreferences
```

## Data Flow

```
User Interaction
      ↓
   Screen (Compose)
      ↓
   ViewModel
      ↓
   Repository
      ↓
   API Client / Database
      ↓
   Fish Audio API / Room DB
```

## State Management

### StateFlow
- **Purpose**: Reactive state emission
- **Usage**: ViewModel exposes state as Flow
- **Collection**: Screens use `collectAsState()` for Compose integration

### Example: Settings Flow
```
SettingsViewModel
  ├── apiKey: StateFlow<String>
  ├── ttsModel: StateFlow<String>
  ├── voiceSpeed: StateFlow<Float>
  └── ...other settings
  
Screen collects these flows and renders UI
When user changes value → updateTemperature() → saved to preferences
```

## Networking

### Retrofit Configuration
```
OkHttpClient
  ├── Logging Interceptor
  ├── Connection Timeout: 120s
  ├── Read Timeout: 120s
  └── Write Timeout: 120s

Retrofit
  ├── Base URL: https://api.fish.audio
  ├── Converter: GsonConverterFactory
  └── Service Interface: FishAudioService
```

### Request Flow
```
generateSpeech()
  ├── Create TTSRequest
  ├── Call service.textToSpeech()
  ├── Receive ResponseBody
  ├── Return InputStream
  └── Consumer plays audio
```

## Database

### Room Schema
```
favorite_voices (Table)
  ├── id: String (PK)
  ├── nickname: String
  ├── referenceId: String
  ├── emotion: String
  ├── isDefault: Boolean
  └── createdAt: Long
```

### DAO Operations
- Insert/Update/Delete voices
- Query by ID or get all
- Get default voice
- Count favorites

## Security

### Encryption
- **SharedPreferences**: EncryptedSharedPreferences (AES-256)
- **API Key**: Stored encrypted, never logged
- **HTTPS**: All API communication encrypted

### Permissions
- INTERNET: Required for API calls
- READ/WRITE_EXTERNAL_STORAGE: Audio file access
- RECORD_AUDIO: Future voice recording feature

## Deep Linking

### URI Schemes
```
fishaudiotts://speak?voice=VoiceNickname&text=TextToSpeak
fishaudiotts://settings
fishaudiotts://discover
```

### Intent Handling
MainActivity processes deep links via intent filters and routes to appropriate screen/action.

## Performance Considerations

1. **Network Optimization**
   - Connection pooling via OkHttp
   - Streaming audio responses
   - Timeout configuration

2. **Database Optimization**
   - Flow-based reactive queries
   - Indexed primary keys
   - Efficient DAO methods

3. **Memory Management**
   - Coroutine scoping with viewModelScope
   - Proper resource cleanup
   - No memory leaks in observers

4. **UI Optimization**
   - Compose recomposition optimization
   - Lazy lists for large datasets
   - Proper state hoisting

## Testing Strategy

### Unit Tests
- ViewModel logic
- Repository operations
- Utility functions

### Integration Tests
- API client with mock server
- Database operations
- Navigation flows

### Manual Testing
- API key validation
- Voice operations
- Settings persistence
- Deep linking

## Future Enhancements

1. **Hilt Dependency Injection**
   - Replace manual DI
   - Improve testability

2. **Paging Library**
   - Handle large voice lists
   - Efficient pagination

3. **WorkManager**
   - Background TTS generation
   - Batch processing

4. **DataStore**
   - Replace SharedPreferences
   - Protobuf serialization

5. **Kotlin Serialization**
   - Replace Gson
   - Better type safety

6. **WebSocket Support**
   - Real-time streaming TTS
   - Reduced latency

## Scalability

The architecture supports:
- Adding new screens (follow existing pattern)
- Multiple data sources (extend Repository)
- Custom voice providers (create new API clients)
- Enhanced theme variations (extend Color/Type files)

## Code Quality

- **Null Safety**: Full Kotlin null-safety
- **Coroutines**: Proper scope management
- **Error Handling**: Try-catch with logging
- **Type Safety**: No casts, proper generics
- **Documentation**: KDoc comments on public APIs
