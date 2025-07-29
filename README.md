# Voice Cloning omidnini1 🎤

## نرم‌افزار کلون کردن صدا (Voice Cloning App)

این نرم‌افزار برای کلون کردن صدای کاربران و تولید محتوای صوتی با لحن‌های مختلف طراحی شده است.

### ✨ ویژگی‌های کلیدی

- **🎯 کلون کردن صدا**: ضبط و تحلیل صدای کاربر
- **🎭 لحن‌های مختلف**: خوشحال، غمگین، عصبانی، هیجان‌زده، آرام و معمولی
- **🌍 پشتیبانی چندزبانه**: زبان فارسی و ۹۹٪ زبان‌های دنیا
- **📱 رابط کاربری مدرن**: طراحی زیبا با پشتیبانی از حالت شب و روز
- **💾 ذخیره و اشتراک**: امکان ذخیره در پوشه موزیک و اشتراک‌گذاری
- **🆓 کاملاً رایگان**: بدون هیچ محدودیتی

### 🛠️ نحوه نصب و راه‌اندازی

#### پیش‌نیازها
- Android Studio (آخرین نسخه)
- JDK 8 یا بالاتر
- Android SDK (API 24 به بالا)

#### مراحل نصب:

1. **دانلود پروژه**:
   ```bash
   git clone https://github.com/your-username/voice-cloning-omidnini1.git
   cd voice-cloning-omidnini1
   ```

2. **باز کردن در Android Studio**:
   - Android Studio را باز کنید
   - "Open an existing project" را انتخاب کنید
   - پوشه پروژه را انتخاب کنید

3. **Sync پروژه**:
   - منتظر بمانید تا Gradle sync کامل شود
   - اگر خطایی وجود داشت، "Sync Now" را کلیک کنید

4. **اجرای برنامه**:
   - دستگاه اندروید را وصل کنید یا emulator راه‌اندازی کنید
   - دکمه "Run" (▶️) را کلیک کنید

### 📦 ساخت فایل APK

#### روش اول: از طریق Android Studio
1. `Build` → `Generate Signed Bundle / APK`
2. `APK` را انتخاب کنید
3. کلید امضا ایجاد کنید یا موجود را انتخاب کنید
4. `release` build variant را انتخاب کنید
5. `Finish` را کلیک کنید

#### روش دوم: از طریق Command Line
```bash
# برای debug APK
./gradlew assembleDebug

# برای release APK
./gradlew assembleRelease
```

فایل APK در مسیر زیر ایجاد می‌شود:
```
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/apk/release/app-release.apk
```

### 📱 نحوه استفاده

#### مرحله ۱: ضبط صدا
1. برنامه را باز کنید
2. مجوزهای لازم را بدهید (ضبط صدا، دسترسی به فایل‌ها)
3. روی دکمه میکروفون کلیک کنید
4. چند جمله واضح بخوانید (حداقل ۳۰ ثانیه)
5. ضبط را متوقف کنید

#### مرحله ۲: انتخاب لحن
- یکی از لحن‌های موجود را انتخاب کنید:
  - **معمولی**: صدای طبیعی
  - **خوشحال**: صدای شاد و پرانرژی
  - **غمگین**: صدای آرام و غمگین
  - **عصبانی**: صدای تند و جدی
  - **هیجان‌زده**: صدای پرهیجان
  - **آرام**: صدای ملایم و آرام

#### مرحله ۳: وارد کردن متن
- متن مورد نظر را در کادر متنی وارد کنید
- حداکثر ۱۰۰,۰۰۰ کاراکتر
- از علائم نگارشی استفاده کنید (ویرگول، نقطه، ...)

#### مرحله ۴: تولید صدا
1. دکمه "تولید صدا" را کلیک کنید
2. منتظر پردازش بمانید
3. پس از تکمیل، صدا آماده است

#### مرحله ۵: ذخیره یا اشتراک
- **دانلود**: فایل در پوشه Music/VoiceCloning ذخیره می‌شود
- **اشتراک**: می‌توانید فایل را در شبکه‌های اجتماعی به اشتراک بگذارید

### 🎨 تنظیم حالت شب/روز

- روی آیکون خورشید/ماه در بالای صفحه کلیک کنید
- تم برنامه به صورت خودکار تغییر می‌کند

### 🔧 تنظیمات پیشرفته

#### اضافه کردن زبان‌های جدید:
در فایل `VoiceCloningViewModel.kt` می‌توانید زبان‌های جدید اضافه کنید:

```kotlin
// برای زبان عربی
textToSpeech?.setLanguage(Locale("ar"))

// برای زبان انگلیسی
textToSpeech?.setLanguage(Locale.US)

// برای زبان فرانسوی
textToSpeech?.setLanguage(Locale.FRENCH)
```

#### تغییر کیفیت صدا:
در فایل `VoiceCloningViewModel.kt`:

```kotlin
// کیفیت بالا
mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
mediaRecorder.setAudioEncodingBitRate(128000)
mediaRecorder.setAudioSamplingRate(44100)
```

### 🐛 رفع مشکلات رایج

#### مشکل در ضبط صدا:
- مطمئن شوید مجوز ضبط صدا داده شده
- میکروفون دستگاه را بررسی کنید
- برنامه‌های دیگر که از میکروفون استفاده می‌کنند را ببندید

#### مشکل در تولید صدا:
- اتصال اینترنت را بررسی کنید
- مطمئن شوید موتور TTS نصب است
- فضای ذخیره‌سازی کافی داشته باشید

#### مشکل در ذخیره فایل:
- مجوز دسترسی به فایل‌ها را بررسی کنید
- فضای ذخیره‌سازی کافی داشته باشید

### 📋 الزامات سیستم

- **حداقل**: Android 7.0 (API 24)
- **توصیه شده**: Android 10.0 (API 29) به بالا
- **RAM**: حداقل 2GB
- **فضای ذخیره**: حداقل 100MB
- **دسترسی**: میکروفون، ذخیره‌سازی، اینترنت

### 🔒 حریم خصوصی

- تمام داده‌ها به صورت محلی ذخیره می‌شوند
- هیچ اطلاعاتی به سرور ارسال نمی‌شود
- صداهای ضبط شده روی دستگاه شما باقی می‌مانند

### 🚀 آپدیت‌های آینده

- [ ] پشتیبانی از AI voice cloning پیشرفته
- [ ] افزودن افکت‌های صوتی
- [ ] قابلیت تنظیم سرعت و pitch دقیق‌تر
- [ ] پشتیبانی از فرمت‌های صوتی بیشتر
- [ ] رابط کاربری بهتر با انیمیشن‌ها

### 📞 پشتیبانی

برای گزارش باگ یا درخواست ویژگی جدید:
- ایمیل: omidnini1@example.com
- GitHub Issues: [لینک]

### 📄 مجوز

این پروژه تحت مجوز MIT منتشر شده است. برای اطلاعات بیشتر فایل LICENSE را مطالعه کنید.

---

## English Version

### 🎤 Voice Cloning omidnini1

A free voice cloning application that records your voice and generates speech with various emotions.

### Key Features
- **Voice Recording & Cloning**: Record and analyze user voice
- **Multiple Emotions**: Happy, sad, angry, excited, calm, and normal
- **Multi-language Support**: Persian and 99% of world languages
- **Modern UI**: Beautiful design with day/night mode
- **Save & Share**: Save to music folder and share capabilities
- **Completely Free**: No limitations

### Installation Guide

1. **Download Android Studio** (latest version)
2. **Clone the project**:
   ```bash
   git clone https://github.com/your-username/voice-cloning-omidnini1.git
   ```
3. **Open in Android Studio**
4. **Sync Gradle**
5. **Run on device or emulator**

### Building APK

```bash
# Debug APK
./gradlew assembleDebug

# Release APK  
./gradlew assembleRelease
```

### Usage Instructions

1. **Record Voice**: Tap microphone and speak clearly for 30+ seconds
2. **Select Emotion**: Choose from 6 available emotions
3. **Enter Text**: Input text (up to 100,000 characters)
4. **Generate Voice**: Tap generate button and wait for processing
5. **Save/Share**: Download to music folder or share via social media

### System Requirements

- **Minimum**: Android 7.0 (API 24)
- **Recommended**: Android 10.0+ (API 29)
- **RAM**: 2GB minimum
- **Storage**: 100MB minimum
- **Permissions**: Microphone, Storage, Internet

### Privacy

- All data stored locally on device
- No data sent to external servers
- Your voice recordings stay on your device

---

**Made with ❤️ by omidnini1**