# native-react-hybrid
Bare bones sample app that is setup in native code and uses a separate Activity for some React-Native flows.


Pre-requisites:

NodeJs using NVM -> Reference: https://tecadmin.net/install-nodejs-with-nvm/

Step 1. curl https://raw.githubusercontent.com/creationix/nvm/master/install.sh | bash

Step 2. source ~/.profile
         OR
        source ~/.bashrc

Step 3. nvm ls-remote

Step 4. nvm install v12.18.2

Step 5. nvm use v12.18.2


Yarn package manager -> Reference: https://classic.yarnpkg.com/en/docs/install/#debian-stable

Step 1. curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | sudo apt-key add -

Step 2. echo "deb https://dl.yarnpkg.com/debian/ stable main" | sudo tee /etc/apt/sources.list.d/yarn.list

Step 3. sudo apt update && sudo apt install yarn

Step 4. Test yarn installation with the command: yarn --version


Watchman file watcher -> Reference: https://facebook.github.io/watchman/docs/install.html

Step 1. Install brew if not already installed: sudo apt install brew

Step 2. brew update

Step 3. brew install watchman

Step 4. Increase inotify file watch limit since React Native needs to watch your project files, so that it can auto-refresh the app when you save source code file.
        Check current limit with: cat /proc/sys/fs/inotify/max_user_watches
        It will give some number as output, like 8192
        Set this to a high value: echo fs.inotify.max_user_watches=524288 | sudo tee -a /etc/sysctl.conf && sudo sysctl -p


Android Studio -> https://developer.android.com/studio

Step 1. Install Android Studio. It comes included with JDK which will also get installed in a sub-directory inside your android studio directory.

Step 2. During the installation, install at-least the latest (Android 10 i.e SDK 29) android version sdk, platform tools, emulator and system image.

Step 3. Configure system variables so that Android sdk and tools are accessible from anywhere.
        sudo gedit ~/.bashrc
        Add the following lines to your $HOME/.bash_profile or $HOME/.bashrc
        export ANDROID_HOME=$HOME/Android/Sdk
        export PATH=$PATH:$ANDROID_HOME/emulator
        export PATH=$PATH:$ANDROID_HOME/tools
        export PATH=$PATH:$ANDROID_HOME/tools/bin
        export PATH=$PATH:$ANDROID_HOME/platform-tools

Step 4. Reload the system variables: source $HOME/.bash_profile

Step 5. Ensure that you do not have multiple installations of Android Debug Bridge i.e adb.
        Enter: whereis adb
        This should return only one path which is in the android studio directory. If multiple are shown, then uninstall the others using the command: sudo apt remove adb


Add React Native to existing native android project: https://reactnative.dev/docs/integration-with-existing-apps

Step 1. Create a new project from Android Studio using whatever core features are needed. eg. Blank Activity, Bottom Navigation, Navigation Drawer Activity, etc.

Step 2. Create a separate new directory for the hybrid project. Note that this directory is NOT to be created inside the android project folder we created in step 1.

Step 3. Create a new directory named android inside the hybrid project directory. Copy all files from the Android project created using Android Studio, into this android directory.
        Note that we have to copy all the files inside the project folder, not the project folder itself.

Step 4. Create a new file package.json at the project root, with following contents:
        {
         "name": "MyReactNativeApp",
         "version": "1.0.0",
         "private": true,
         "scripts": {
          "start": "yarn react-native start"
         }
        }

Step 5. Add react and react-native to our project:
        Open terminal in project root and execute:
        yarn add react-native
        yarn add react
        This will have created a node_modules folder in the project.

Step 6. Copy the .gitIgnore file from android directory into the project root directory and edit it so that it looks like this:
        /node_modules
        /android/*.iml
        /android/.gradle
        /android/local.properties
        /android/.idea/caches
        /android/.idea/libraries
        /android/.idea/modules.xml
        /android/.idea/workspace.xml
        /android/.idea/navEditor.xml
        /android/.idea/assetWizardSettings.xml
        /android/.DS_Store
        /android/build
        /android/captures
        /android/.externalNativeBuild
        /android/.cxx

Step 7. Add to app build.gradle file:
        implementation "com.facebook.react:react-native:+" // From node_modules
        implementation "org.webkit:android-jsc:+"

Step 8. Add to project build.gradle file:
        allprojects {
        repositories {
        maven {
            // All of React Native (JS, Android binaries) is installed from npm
            url "$rootDir/../node_modules/react-native/android"
        }
        maven {
            // Android JSC is installed from npm
            url("$rootDir/../node_modules/jsc-android/dist")
        }
        ...
        }
        ...
        }

Step 9. Add to settings.gradle file:
        apply from: file("../node_modules/@react-native-community/cli-platform-android/native_modules.gradle"); applyNativeModulesSettingsGradle(settings)

Step 10. Add at the bottom of app build.gradle file:
         apply from: file("../../node_modules/@react-native-community/cli-platform-android/native_modules.gradle"); applyNativeModulesAppBuildGradle(project)

Step 11. Add permissions to AndroidManifest.xml
         <uses-permission android:name="android.permission.INTERNET" />

Step 12. Add a debug folder inside src folder, so that it is a sibling of the folder named main
         Copy and Paste the AndroidManifest.xml file from main folder into the debug folder. Android Studio will now use this AndroidManifest.xml file for debug builds,
         allowing us to configure things for only the debug builds.
         Create directory named res inside debug folder.
         Create directory named xml inside this res folder.
         Create network_security_config.xml inside this xml folder.

Step 13. Configure the debug/AndroidManifest.xml: 
         Add the dev settings activity: <activity android:name="com.facebook.react.devsupport.DevSettingsActivity" />
         Add your Activity which you plan to use for loading React-Native and set its theme to NoActionBar:
         <activity android:name=".ui.hybrid.HybridActivity"
            android:theme="@style/ReactActivityTheme" />
         Add Overlay Permission since React Native errors are to be shown above all other windows: <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
         Set the network_security_config attribute in application tag: android:networkSecurityConfig="@xml/network_security_config"

Step 14. Configure the network_security_config.xml:
         <network-security-config>
          <domain-config cleartextTrafficPermitted="true">
           <domain includeSubdomains="true">localhost</domain>
          </domain-config>
         </network-security-config>
         Note that we are allowing clearTextTraffic only in the debug network_security_config, since react-native code is loaded from a localhost http server in debug builds.

Step 15. Create index.js file in project root with following code:
        import React from 'react';
	import {
	  AppRegistry,
	  StyleSheet,
	  Text,
	  View
	} from 'react-native';

	class HelloWorld extends React.Component {
	  render() {
	    return (
	      <View style={styles.container}>
	        <Text style={styles.hello}>Hello, World</Text>
	      </View>
	    );
	  }
	}
	var styles = StyleSheet.create({
	  container: {
	    flex: 1,
	    justifyContent: 'center'
	  },
	  hello: {
	    fontSize: 20,
	    textAlign: 'center',
	    margin: 10
	  }
	});
	
	AppRegistry.registerComponent(
	  'MyReactNativeApp',
	  () => HelloWorld
	);


Step 16. The Activity that you created for showing react-native components, should extend Activity class and not AppCompatActivity, since the latter requires super to be called from onActivityResult.

Step 17. Request Runtime Permission in debug build for Overlay permission:
	private final int OVERLAY_PERMISSION_REQ_CODE = 1;  // Choose any value
	...

	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
	    if (!Settings.canDrawOverlays(this)) {
	        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
	                                   Uri.parse("package:" + getPackageName()));
	        startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
	    }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
	        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
	            if (!Settings.canDrawOverlays(this)) {
	                // SYSTEM_ALERT_WINDOW permission not granted
	            }
	        }
	    }
	    mReactInstanceManager.onActivityResult( this, requestCode, resultCode, data );
	}

Step 18. Configure your activity to render react-native components:
	public class MyReactActivity extends Activity implements DefaultHardwareBackBtnHandler {
	    private ReactRootView mReactRootView;
	    private ReactInstanceManager mReactInstanceManager;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
        	super.onCreate(savedInstanceState);
        	SoLoader.init(this, false);
	
        	mReactRootView = new ReactRootView(this);
        	List<ReactPackage> packages = new PackageList(getApplication()).getPackages();
		// PackageList class will be available once you build the project.
        	// Packages that cannot be autolinked yet can be added manually here, for example:
        	// packages.add(new MyReactNativePackage());
        	// Remember to include them in `settings.gradle` and `app/build.gradle` too.
	
        	mReactInstanceManager = ReactInstanceManager.builder()
        	        .setApplication(getApplication())
        	        .setCurrentActivity(this)
        	        .setBundleAssetName("index.android.bundle")
        	        .setJSMainModulePath("index")
        	        .addPackages(packages)
        	        .setUseDeveloperSupport(BuildConfig.DEBUG)
        	        .setInitialLifecycleState(LifecycleState.RESUMED)
        	        .build();
        	// The string here (e.g. "MyReactNativeApp") has to match
        	// the string in AppRegistry.registerComponent() in index.js
        	mReactRootView.startReactApplication(mReactInstanceManager, "MyReactNativeApp", null);
	
        	setContentView(mReactRootView);
    	}
	
	    @Override
	    public void invokeDefaultOnBackPressed() {
        	super.onBackPressed();
	    }
	}

	@Override
	protected void onPause() {
	    super.onPause();
	
	    if (mReactInstanceManager != null) {
	        mReactInstanceManager.onHostPause(this);
	    }
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	
	    if (mReactInstanceManager != null) {
	        mReactInstanceManager.onHostResume(this, this);
	    }
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	
	    if (mReactInstanceManager != null) {
	        mReactInstanceManager.onHostDestroy(this);
	    }
	    if (mReactRootView != null) {
	        mReactRootView.unmountReactApplication();
	    }
	}

	@Override
	 public void onBackPressed() {
	    if (mReactInstanceManager != null) {
	        mReactInstanceManager.onBackPressed();
	    } else {
	        super.onBackPressed();
	    }
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_MENU && mReactInstanceManager != null) {
	        mReactInstanceManager.showDevOptionsDialog();
	        return true;
	    }
	    return super.onKeyUp(keyCode, event);
	}


Step 19. Start your hybrid activity on some event like a button click and you can pass Bundle data as you would do to a normal native Activity.

Step 20. Pass the Intent bundle data to React-Native so that it can access it via props in the first component.
	Change this line mReactRootView.startReactApplication(mReactInstanceManager, "MyReactNativeApp", null);
	to mReactRootView.startReactApplication(mReactInstanceManager, "MyReactNativeApp", getIntent().getExtras());

Step 21. run yarn start in project root

Step 22. [For Android 9 and Above] Open another terminal and run: adb reverse tcp:8081 tcp:8081

Step 23. Run your native project from android studio and trigger the event that will open your hybrid activity.
