# Sensely Android Conversational SDK

<a href="https://tinyurl.com/senselysdk"><img src="https://cl.ly/ca1a088639e6/request-access-button.png" alt="Request access" width="157"></a>

The Sensely Android SDK is designed to allow you to easily incorporate custom avatar technology and conversational content in your Android app. Start using the SDK today to leverage the power of conversational language interfaces. The avatar supports both voice and text options, allowing you to intelligently communicate with your patient and insurance plan member populations.

## Technical overview

The Sensely SDK can be embedded in an existing iOS or Android app. When calling the SDK, the partner app will get a conversation flow (e.g. mayo symptom checker). Here is an overview of the integration:

* The SDK can run in anonymous mode (no unique user, Sensely does not store responses) or with an authenticated user.
* The partner app activates the Sensely UI and begin the conversation experience:
    * The user proceeds through the conversation by interacting with the Sensely UI which generally takes up the full screen inside the app.
    * During the conversation, the sensely conversation engine will direct a user through a flow, described below.
    * At any point, the Sensely SDK can be directed to pause the conversation and return to the partner app using a callback. This is often used to invoke features from the partner app during a conversation such as showing a directory of in-network services or appointment booking flow. 
* Once the conversation has been completed:
    * The results of the conversation are returned to the consuming app in JSON format. These results can also be recorded in Sensely’s backend if desired.
    * The user is transitioned back to the partner app fully.

## Integration with Mayo Clinic symptom assessment tool

Sensely is ready to use with the Mayo Clinic symptom assessment tool in 3 languages (contact us for more languages). The introduction can be reviewed and customized with disclaimers by the partner’s team. The Mayo symptom checker also supports using the symptom checker on behalf of a 3rd party (child, other family member).

In addition to symptom checker interactions, partners are able to build other conversation interactions using either our conversation design tools or ad-hoc using a specification of the conversation definition and building the conversation programmatically. More documentation is available upon request. 

## Technical quickstart

### Prerequisites
* Android Studio 3.0+
* minSdkVersion 19
* targetSdkVersion 27+

Follow these directions:
* Note: Android versions 5.0 (and higher) are supported
* Clone this repo
* In Main menu of Android Studio go File->New->Import Project
* Build and run the app
* When the app loads, provide your username and password to see available conversations - you can request a demo license to get access.

<a href="https://tinyurl.com/senselysdk"><img src="https://cl.ly/ca1a088639e6/request-access-button.png" alt="Request access" width="157"></a>

## License

The Sensely Android SDK is released under the Sensely license. See the LICENSE file for more details.

[1] : https://developer.android.com/studio/build/multidex.html <br>
[2] : https://developer.android.com <br>
[3] : https://developer.android.com/studio/projects/add-app-module.html <br>
[4] : https://developer.android.com/studio/projects/android-library.html#publish_multiple_variants

## Other platforms of the Sensely SDK
* [iOS](https://github.com/Sensely/SDK-iOS/)
* [Web](https://github.com/Sensely/SDK-Web)
