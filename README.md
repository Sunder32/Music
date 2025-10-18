# 🎵 Music

Современное Android-приложение для генерации уникальной музыки.

<img width="576" height="800" alt="image" src="https://github.com/user-attachments/assets/bff8bae7-6a8b-4e18-9078-1412607394d5" />
<img width="576" height="800" alt="image" src="https://github.com/user-attachments/assets/aacf9ebb-5b0d-4f3c-8c2e-cf35fc41f91c" />
<img width="576" height="800" alt="image" src="https://github.com/user-attachments/assets/23730997-287d-4df0-96f3-b88663b74ac8" />

## ✨ Особенности

### 🎨 Современный дизайн
- **Темная тема** с градиентными эффектами
- **Material Design 3** компоненты
- **Анимированный интерфейс** с плавными переходами
- **Визуализация** в виде анимированной волновой формы

### 🎼 Функционал
- **Генерация музыки по описанию** - опишите желаемый трек текстом
- **8 жанров**: Electronic, Hip-Hop, Rock, Jazz, Classical, Ambient, Pop, Lo-fi
- **8 настроений**: Energetic, Calm, Happy, Melancholic, Romantic, Dark, Epic, Dreamy
- **История треков** - все созданные треки сохраняются
- **Детальная информация** о каждом треке
- **Красивые карточки** с уникальными цветами для каждого настроения

### 🤖 AI-технологии
- Использует **OpenRouter API**
- Модель: **Meta Llama 3.2 3B Instruct**

## 🏗️ Архитектура

```
app/
├── data/
│   ├── model/          # Data models
│   ├── api/            # API services & Retrofit
│   └── repository/     # Repository pattern
├── ui/
│   ├── screens/        # Compose screens
│   ├── components/     # Reusable UI components
│   ├── viewmodel/      # ViewModels
│   └── theme/          # Theme & colors
└── MainActivity.kt
```

## 🎨 Цветовая палитра

- **Background**: Градиент от #0f0c29 через #302b63
- **Accent Purple**: #9d4edd
- **Accent Pink**: #ff006e
- **Accent Blue**: #3a86ff
- **Accent Green**: #06ffa5
- **Accent Orange**: #fb5607

## 🚀 Технологии

- **Kotlin** - основной язык
- **Jetpack Compose** - современный UI toolkit
- **Material 3** - дизайн система
- **Retrofit** - HTTP клиент
- **Coroutines & Flow** - асинхронность
- **ViewModel** - архитектура
- **Coil** - загрузка изображений
- **Media3 ExoPlayer** - плеер 

## 📱 Использование

1. **Опишите музыку** - введите текстовое описание желаемого трека
2. **Выберите жанр** - Electronic, Hip-Hop, Rock и другие
3. **Выберите настроение** - Energetic, Calm, Happy и другие
4. **Нажмите "Generate Music"** - AI создаст концепцию трека
5. **Просмотрите историю** - все треки сохраняются

## 🔑 API Key

Приложение использует OpenRouter API. Ключ уже встроен в код:
```kotlin
// data/api/RetrofitInstance.kt
private const val API_KEY = "sk-or-v1-..."
```
