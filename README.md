# ArchVisionAI

This application was created as a component of Ruhr University Bochum's Software Engineering Labs program. Using the app, hand-drawn diagrams can be photographed and then digitised. The diagrams are then outputed in PlantUML code and displayed as an image. Further changes can also be made to the diagrams. Various large language models can be used for this, such as Gemini or ChatGPT4o.

## SDK Version
- **SDK Version:** This project runs on Android 14.0. There is no guarantee that it will also run on older devices.

## Setup
To get the app running in your environment, follow the instructions below.
### 1.  Installation
Clone this repository and open it in Android Studio
### 2. Create api keys
The app uses various large language models, for example Gemini or ChatGPT4o. Api keys must be created for these.
[ChatGPT](https://platform.openai.com/docs/quickstart)
[Gemini](https://ai.google.dev/gemini-api/docs/api-key?hl=de)
### 3. Insert Api keys for tests (optional)
In the TestClass copy the api key into the two methods: setApikeyForGPT4o and setApikeyForGemini. 

**Important: Only add api keys to the code if no other person can access it!**

## How to use this App
Please view the demo video found in the {demo} folder to get an idea of how to use this app.


## License

This app is published under the MIT License.
