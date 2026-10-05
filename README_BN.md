# Jain AI — Android Edition

**Target:** Samsung A20 / lightweight Android client

## Included
- Jain (জেইন) identity
- Anime avatar
- Bengali text chat
- Android SpeechRecognizer voice input
- Android TextToSpeech voice output
- Offline-first fallback
- Optional local LLM endpoint integration
- Minimal dependency footprint

## Build
Android Studio দিয়ে project open করো এবং Gradle sync/build করো। এই environment-এ Android SDK/Gradle wrapper নেই, তাই এখানে APK compile করা হয়নি।

## Local AI
Default endpoint: `http://127.0.0.1:11434/api/chat` এবং model `qwen2.5:1.5b`। Phone-এ সত্যিকারের on-device LLM চাইলে Android-compatible llama.cpp/MLC runtime এবং একটি quantized model যুক্ত করতে হবে। এই ZIP-এ model weights নেই, কারণ সেগুলো hardware/model-license অনুযায়ী আলাদা এবং অনেক বড় হতে পারে।

## Voice
Phone-এর built-in SpeechRecognizer + TextToSpeech ব্যবহার করা হয়েছে যাতে A20-তে lightweight থাকে। Supplied reference voice asset রাখা হয়েছে future compatible voice engine integration-এর জন্য। Android-এর built-in TTS নিজে থেকে supplied voice clone করবে না।
