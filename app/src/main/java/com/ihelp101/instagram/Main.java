package com.ihelp101.instagram;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActionBarDrawerToggle drawerToggle;
    Adapter mAdapter;
    ListView listView;

    static int FILE_CODE = 0;
    String currentAction;
    String getDirectory = Environment.getExternalStorageDirectory().toString();
    String saveLocation = "None";
    String saveSD;
    String version = "123";

    String changeOrder;
    String changeVersion;
    String comment;
    String contact;
    String customization;
    String date;
    String defaultSource;
    String errorLog;
    String file;
    String folder;
    String follow;
    String like;
    String lockFeed;
    String logging;
    String hide;
    String misc;
    String notifHide;
    String push;
    String sound;
    String slide;
    String story;
    String suggestion;
    String translate;

    boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    boolean isSDCard(String saveLocation) {
        boolean result = false;
        try {
            File root = new File(saveLocation, ".Instagram");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "SD.txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append("SD Card Check");
            writer.flush();
            writer.close();

            result = false;
        } catch (FileNotFoundException e) {
            result = true;
        } catch (Exception e) {
        }

        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        drawerLayout.openDrawer(GravityCompat.START);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            saveLocation = "None";
        }

        if (requestCode == 12 && resultCode == Activity.RESULT_OK) {
            Helper.setSetting(saveLocation, data.getDataString() + ";" + saveSD);
            if (android.os.Build.VERSION.SDK_INT >= 19) {
                getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            saveLocation = "None";
        }

        if (requestCode == FILE_CODE && resultCode == Main.RESULT_OK) {
            Uri Location = null;
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Location = clip.getItemAt(i).getUri();
                        }
                    }
                    // For Ice Cream Sandwich
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path : paths) {
                            Location = Uri.parse(path);
                        }
                    }
                }

            } else {
                Location = data.getData();
            }

            String toast;

            try {
                if (Location.toString().contains("/storage/") || Location.toString().contains("/mnt/") || Location.toString().contains("/sdcard/")) {
                    if (!isSDCard(Location.getPath())) {
                        Helper.setSetting(saveLocation, Location.getPath() + "/");
                        saveLocation = "None";
                    } else if (!Helper.getSaveLocation(saveLocation).contains("com.android.externalstorage.documents")){
                        saveSD = Location.getPath() + "/";
                        startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), 12);
                    } else {
                        Helper.setSetting(saveLocation, Helper.getSaveLocation(saveLocation).split(";")[0] + ";" + Location.getPath() + "/");
                        saveLocation = "None";
                    }
                } else {
                    try {
                        toast = Helper.getResourceString(getApplicationContext(), R.string.Incorrect_Location);
                    } catch (Throwable t) {
                        toast = "Unable to save here.\nPlease select another location.";
                    }

                    setToast(toast);

                    Intent i = new Intent(Main.this, FilePickerActivity.class);
                    i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                    i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
                    i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
                    startActivityForResult(i, FILE_CODE);
                }
            } catch (Exception e) {
            }
        }

        if (requestCode == FILE_CODE && resultCode == 123456) {
            Helper.setSetting(saveLocation, "Instagram");
            String reset;

            try {
                reset = Helper.getResourceString(getApplicationContext(), R.string.Reset);
            } catch (Throwable t) {
                reset = "Unable to save here.\nPlease select another location.";
            }

            Toast.makeText(getApplicationContext(), reset + " " + currentAction, Toast.LENGTH_SHORT).show();
        }
    }

    class HookCheck extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            String responseString = "Nope";

            try {
                URL u = new URL(uri[0]);
                URLConnection c = u.openConnection();
                c.connect();

                InputStream inputStream = c.getInputStream();

                responseString = Helper.convertStreamToString(inputStream);
            } catch (Exception e) {
                responseString = "Nope";
                Helper.setError("Hook Fetch Failed - " +e);
            }


            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
                for (int i = 0; i < packs.size(); i++) {
                    PackageInfo p = packs.get(i);
                    if (p.packageName.equals("com.instagram.android")) {
                        version = Integer.toString(p.versionCode);
                        version = version.substring(0, version.length() - 2);
                    }
                }

                String text = Helper.getSetting("Hooks");

                String toast;
                try {
                    toast = Helper.getResourceString(getApplicationContext(), R.string.Hooks_Updated);
                } catch (Throwable t) {
                    toast = "Hooks have been updated.\nPlease kill the Instagram app.";
                }

                String[] html = result.split("<p>");

                String matched = "No";

                int count = 0;
                int max = 0;
                for (String data : html) {
                    max++;
                }


                for (String data : html) {
                    count++;

                    String finalCheck = "123";

                    if (!data.isEmpty()) {
                        String[] PasteVersion = data.split(";");
                        finalCheck = PasteVersion[0];
                    }

                    if (version.equals(finalCheck) && !data.isEmpty()) {
                        data = data.replace("<p>", "");
                        data = data.replace("</p>", "");
                        if (data.trim().equals(text.trim())) {
                            try {
                                toast = Helper.getResourceString(getApplicationContext(), R.string.Hooks_Latest);
                            } catch (Throwable t) {
                                toast = "You already have the latest hooks.";
                            }
                        } else {
                            Helper.setSetting("Hooks", data);
                        }
                        matched = "Yes";
                    } else {
                        if (count == max && matched.equals("No")) {
                            System.out.println("Trying default hook!");
                            String fallback = html[1];
                            fallback = fallback.replace("<p>", "");
                            fallback = fallback.replace("</p>", "");
                            fallback = fallback.replaceAll("[0-9]", "");
                            String SavedHooks = text.replaceAll("[0-9]", "");
                            if (fallback.trim().equals(SavedHooks.trim())) {
                                try {
                                    toast = Helper.getResourceString(getApplicationContext(), R.string.Hooks_Latest);
                                } catch (Throwable t) {
                                    toast = "You already have the latest hooks.";
                                }
                            } else {
                                Helper.setSetting("Hooks", fallback);
                                try {
                                    toast = Helper.getResourceString(getApplicationContext(), R.string.Hooks_Updated);
                                } catch (Throwable t) {
                                    toast = "Hooks have been updated.\nPlease kill the Instagram app.";
                                }
                            }
                        } else if (!data.isEmpty() && matched.equals("No")) {
                            int typeCheck = Integer.parseInt(version) - Integer.parseInt(finalCheck);
                            if (typeCheck <= 2 && typeCheck >= -2) {
                                if (data.trim().equals(text.trim())) {
                                    try {
                                        toast = Helper.getResourceString(getApplicationContext(), R.string.Hooks_Latest);
                                    } catch (Throwable t) {
                                        toast = "You already have the latest hooks.";
                                    }
                                } else {
                                    Helper.setSetting("Hooks", data);
                                }
                                matched = "Yes";
                            }
                        }
                    }
                }
                setToast(toast);
            } catch (Exception e) {
                Helper.setError("Hooks Parse Failed - " +e);
            }
        }
    }

    class Translation extends AsyncTask<String, String, String> {
        String response = "Nope";
        String responseDefaultLanguage = "None";
        String responseLanguage = "None";
        String userLocale;

        @Override
        protected String doInBackground(String... uri) {
            try {
                userLocale = Resources.getSystem().getConfiguration().locale.toString() + ";";
                URL u = new URL("https://raw.githubusercontent.com/iHelp101/XInsta/master/Translations.txt");
                URLConnection c = u.openConnection();
                c.connect();

                response = Helper.convertStreamToString(c.getInputStream());

                u = new URL("https://raw.githubusercontent.com/iHelp101/XInsta/master/Translate.txt");
                c = u.openConnection();
                c.connect();
                responseDefaultLanguage = Helper.convertStreamToString(c.getInputStream());

                try {
                    u = new URL("https://raw.githubusercontent.com/iHelp101/XInsta/master/app/src/main/res/values-" + userLocale.replace(";", "") + "/strings.xml");
                    c = u.openConnection();
                    c.connect();
                    responseLanguage = Helper.convertStreamToString(c.getInputStream());
                } catch (Exception e) {
                    responseLanguage = "None";
                }
            } catch (Exception e) {
                Helper.setError("Translation - " + e);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                System.out.println("Info: " +responseLanguage);
                if (response.contains(userLocale) && !responseLanguage.equals("None")) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    System.out.println("Yes");
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"XInsta@ihelp101.com"});
                                    intent.putExtra(Intent.EXTRA_SUBJECT, "XInsta - Translation");
                                    intent.putExtra(Intent.EXTRA_TEXT, responseLanguage);
                                    intent.setType("text/plain");
                                    startActivity(Intent.createChooser(intent, "Email"));
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    intent = new Intent(Intent.ACTION_SEND);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"XInsta@ihelp101.com"});
                                    intent.putExtra(Intent.EXTRA_SUBJECT, "XInsta - Translation");
                                    intent.putExtra(Intent.EXTRA_TEXT, responseDefaultLanguage);
                                    intent.setType("text/plain");
                                    startActivity(Intent.createChooser(intent, "Email"));
                                    break;
                            }
                        }
                    };

                    String userLanguage = Locale.getDefault().getDisplayLanguage();
                    userLanguage = userLanguage.substring(0, 1).toUpperCase() + userLanguage.substring(1);

                    AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
                    builder.setMessage("Will you be translating " + userLanguage + "?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"XInsta@ihelp101.com"});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "XInsta - Translation");
                    intent.putExtra(Intent.EXTRA_TEXT, responseDefaultLanguage);
                    intent.setType("text/plain");
                    startActivity(Intent.createChooser(intent, "Email"));
                }
            } catch (Exception e) {
                Helper.setError("Translation2 - " + e);
            }
        }
    }

    class VersionCheck extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            String responseString = "Nope";

            try {
                Random r = new Random();
                int cacheInt = r.nextInt(9999999 - 1) + 9999999;

                URL u = new URL("https://raw.githubusercontent.com/iHelp101/XInsta/master/Version.txt?" +cacheInt);
                URLConnection c = u.openConnection();
                c.connect();

                InputStream inputStream = c.getInputStream();

                responseString = Helper.convertStreamToString(inputStream);

            } catch (Exception e) {
                responseString = "Nope";
            }

            return responseString.trim();
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);

            try {
                version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                version = version.trim();

                if (result.equals(version)) {
                    Toast.makeText(getApplicationContext(), "XInsta Up-To-Date", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);

                    builder.setTitle("Update XInsta To " +result+ "?");

                    builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Random r = new Random();
                            int cacheInt = r.nextInt(9999999 - 1) + 9999999;

                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse("https://raw.githubusercontent.com/iHelp101/XInsta/master/XInsta.apk?" +cacheInt));
                            request.setTitle("XInsta " +result+ " Update");
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                                request.allowScanningByMediaScanner();
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            }
                            request.setMimeType("application/vnd.android.package-archive");
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Insta-" + result + ".apk");

                            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                            manager.enqueue(request);
                            dialog.dismiss();
                        }
                    });

                    builder.setPositiveButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.show();
                }
            } catch (Throwable t) {
            }
        }
    }

    void checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED | ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Main.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                setupNav();
            }
        } else {
            setupNav();
        }
    }

    void listAction() {
        String image;
        String profile;
        String video;

        try {
            image = Helper.getResourceString(getApplicationContext(), R.string.Image);
        } catch (Throwable t) {
            image = "Image Location";
        }

        try {
            profile = Helper.getResourceString(getApplicationContext(), R.string.Profile);
        } catch (Throwable t) {
            profile = "Profile Location";
        }
        try {
            video = Helper.getResourceString(getApplicationContext(), R.string.Video);
        } catch (Throwable t) {
            video = "Video Location";
        }

        if (currentAction.equals(image) || currentAction.equals(profile) || currentAction.equals(video)) {
            saveLocation = "Image";
            if (currentAction.equals(profile)) {
                saveLocation = "Profile";
            } else if (currentAction.equals(video)) {
                saveLocation = "Video";
            }

            String save = Helper.getSaveLocation(saveLocation);
            save = save.replace("file://", "").replaceAll("%20", " ");

            if (save.contains("com.android.externalstorage.documents") && android.os.Build.VERSION.SDK_INT >= 21) {
                save = save.split(";")[1];
            }

            System.out.println("Info: " +save);

            Intent i = new Intent(Main.this, FilePickerActivity.class);
            i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
            i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
            i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
            if (!save.equals("Instagram")) {
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, save);
            } else {
                File directory = new File(URI.create(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Instagram").getPath());
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Instagram");
            }
            startActivityForResult(i, FILE_CODE);
        }
        if (currentAction.equals("GitHub")) {
            new HookCheck().execute("https://raw.githubusercontent.com/iHelp101/XInsta/master/Hooks.txt");
        }
        if (currentAction.equals("Pastebin")) {
            new HookCheck().execute("http://pastebin.com/raw.php?i=sTXbUFcx");
        }
        if (currentAction.equals("Alternate Source")) {
            new HookCheck().execute("http://www.snapprefs.com/xinsta/Hooks.txt");
        }
        if (currentAction.equals("One Tap Video Download")) {
            if (appInstalledOrNot("com.phantom.onetapvideodownload")) {
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.phantom.onetapvideodownload");
                startActivity(intent);
            } else {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.phantom.onetapvideodownload")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.phantom.onetapvideodownload")));
                }
            }
        }
        if (currentAction.equals("Zoom For Instagram")) {
            if (appInstalledOrNot("com.Taptigo.XposedModules.IgZoom")) {
                Intent intent = new Intent("de.robv.android.xposed.installer.OPEN_SECTION");
                intent.setPackage("de.robv.android.xposed.installer");
                intent.putExtra("section", "modules");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://repo.xposed.info/module/com.Taptigo.XposedModules.IgZoom"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        listView = (ListView) findViewById(R.id.list);
        updateListView();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.AM,  R.string.Date) {
            public void onDrawerClosed(View view) {
            }

            public void onDrawerOpened(View drawerView) {
            }
        };

        drawerToggle.syncState();

        checkPermission();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                currentAction = mAdapter.getItem(position);
                listAction();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        String clicked = (String) menuItem.getTitle();

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        drawerLayout.closeDrawers();

        String checkVersion;

        try {
            checkVersion = Helper.getResourceString(getApplicationContext(), R.string.Check);
        } catch (Throwable t) {
            checkVersion = getResources().getString(R.string.Check);
        }

        if (clicked.equals(checkVersion)) {
            new VersionCheck().execute();
        }

        String customLanguage;

        try {
            customLanguage = Helper.getResourceString(getApplicationContext(), R.string.Custom);
        } catch (Throwable t) {
            customLanguage = getResources().getString(R.string.Custom);
        }

        if (clicked.equals(customLanguage)) {
            openCustomLanguages();
        }

        String date;

        try {
            date = Helper.getResourceString(getApplicationContext(), R.string.Date);
        } catch (Throwable t) {
            date = getResources().getString(R.string.Date);
        }

        if (clicked.equals(date)) {
            if (Helper.getSetting("Date").equals("Instagram")) {
                if (Helper.getSettings("Hour")) {
                    Helper.setSetting("Date", "MM;/dd;/yyyy; ;HH:;mm:;ss:;a;");
                } else {
                    Helper.setSetting("Date", "MM;/dd;/yyyy; ;hh:;mm:;ss:;a;");
                }
            }
            Intent myIntent = new Intent(getApplicationContext(), com.ihelp101.instagram.Date.class);
            startActivity(myIntent);
        }

        String defaultSource;

        try {
            defaultSource = Helper.getResourceString(getApplicationContext(), R.string.Default);
        } catch (Throwable t) {
            defaultSource = getResources().getString(R.string.Default);
        }

        if (clicked.equals(defaultSource)) {
            final CharSequence items[] = {"GitHub", "Pastebin", "Alternate Source"};

            int itemSelected;
            if (Helper.getSetting("Source").equals("Instagram") || Helper.getSetting("Source").equals("GitHub")) {
                itemSelected = 0;
            } else if (Helper.getSetting("Source").equals("Pastebin")) {
                itemSelected = 1;
            } else if (Helper.getSetting("Source").equals("Alternate")) {
                itemSelected = 2;
            } else {
                itemSelected = 0;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
            builder.setSingleChoiceItems(items, itemSelected, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Helper.setSetting("Source", String.valueOf(items[which]));
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }

        String fileDate;

        try {
            fileDate = Helper.getResourceString(getApplicationContext(), R.string.File);
        } catch (Throwable t) {
            fileDate = getResources().getString(R.string.File);
        }

        if (clicked.equals(fileDate)) {
            fileDate();
        }

        String follow;

        try {
            follow = Helper.getResourceString(getApplicationContext(), R.string.Follow);
        } catch (Throwable t) {
            follow = getResources().getString(R.string.Follow);
        }

        if (clicked.equals(follow)) {
            String color = Helper.getSetting("Color");
            if (color.equals("Instagram")) {
                color = "#2E978C";
            }
            ColorPicker cpd = new ColorPicker(Main.this, Color.parseColor(color), new ColorPicker.OnColorSelectedListener() {
                @Override
                public void colorSelected(Integer color) {
                    String hexColor = String.format("#%06X", (0xFFFFFF & color));
                    Helper.setSetting("Color", hexColor);
                }
            });
            cpd.setTitle("Pick a color.");
            cpd.show();
        }

        String manual;

        try {
            manual = Helper.getResourceString(getApplicationContext(), R.string.Manual);
        } catch (Throwable t) {
            manual = getResources().getString(R.string.Manual);
        }

        if (clicked.equals(manual)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Paste The New Hooks");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Helper.setSetting("Hooks", input.getText().toString());
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }

        String errorLog;

        try {
            errorLog = Helper.getResourceString(getApplicationContext(), R.string.Error_log);
        } catch (Throwable t) {
            errorLog = getResources().getString(R.string.Error_log);
        }

        if (clicked.equals(errorLog)) {
            try {
                sendErrorLog();
            } catch (Exception e) {
                setToast("Failed To Obtain Logs");
            }
        }

        String specialThanks;

        try {
            specialThanks = Helper.getResourceString(getApplicationContext(), R.string.Special);
        } catch (Throwable t) {
            specialThanks = getResources().getString(R.string.Special);
        }

        if (clicked.equals(specialThanks)) {
            Intent myIntent = new Intent(getApplicationContext(), Thanks.class);
            startActivity(myIntent);
        }

        String translations;

        try {
            translations = Helper.getResourceString(getApplicationContext(), R.string.Translations);
        } catch (Throwable t) {
            translations = getResources().getString(R.string.Translations);
        }

        if (clicked.equals(translations)) {
            try {
                new Translation().execute();
            } catch (Exception e) {
            }
        }

        return false;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupNav();
                } else {
                    setToast("Permission denied. Unable to save hook file, image locations, and preferences.");
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupNav();
    }

    void fileDate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);

        final CharSequence items[] = {"Day/Month/Year", "Month/Day/Year", "Year/Month/Day", "Year/Day/Month"};

        int itemSelected;
        if (Helper.getSetting("File").equals("Instagram") || Helper.getSetting("File").equals("Day/Month/Year")) {
            itemSelected = 0;
        } else if (Helper.getSetting("File").equals("Month/Day/Year")) {
            itemSelected = 1;
        } else if (Helper.getSetting("File").equals("Year/Month/Day")) {
            itemSelected = 2;
        } else if (Helper.getSetting("File").equals("Year/Day/Month")) {
            itemSelected = 3;
        } else {
            itemSelected = 0;
        }

        builder.setSingleChoiceItems(items, itemSelected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Helper.setSetting("File", String.valueOf(items[which].toString()));
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    void openCustomLanguages() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);

        final CharSequence items[] = {"Kurdish"};

        int itemSelected = -1;

        if (Helper.getSetting("Language").equals("kurdish")) {
            itemSelected = 0;
        }


        builder.setSingleChoiceItems(items, itemSelected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Helper.setSetting("Language", Helper.getRawString(getApplicationContext()));
                setupNav();
                updateListView();

                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    void sendErrorLog() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"XInsta@ihelp101.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "XInsta - Error Log");
        intent.putExtra(Intent.EXTRA_TEXT, "Please check out my XInsta Error Log.");
        intent.setType("text/plain");

        File root = new File(getDirectory, ".Instagram");
        File file = new File(root, "Error.txt");
        Uri uri = Uri.fromFile(file);
        if (!file.exists() || !file.canRead()) {
            setToast("Error Log Missing!");
            return;
        }

        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Email"));
    }

    void setToast(String message) {
        Toast toast = Toast.makeText(Main.this, message, Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

    void setupNav() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        for (int menuSize = 0; menuSize < navigationView.getMenu().size();menuSize++) {
            for (int subMenuSize = 0; subMenuSize < navigationView.getMenu().getItem(menuSize).getSubMenu().size(); subMenuSize++) {

                final MenuItem menuItem = navigationView.getMenu().getItem(menuSize).getSubMenu().getItem(subMenuSize);
                final String menuTitle = navigationView.getMenu().getItem(menuSize).getSubMenu().getItem(subMenuSize).getTitle().toString();

                final SwitchCompat item = (SwitchCompat) menuItem.getActionView();

                if (menuTitle.equals(getResources().getString(R.string.ChangeOrder))) {
                    try {
                        changeOrder = Helper.getResourceString(getApplicationContext(), R.string.ChangeOrder);
                    } catch (Throwable t) {
                        changeOrder = getResources().getString(R.string.ChangeOrder);
                    }

                    menuItem.setTitle(changeVersion);
                    if (Helper.getSettings("Order")) {
                        item.setChecked(true);
                    }
                }

                if (menuTitle.equals(getResources().getString(R.string.Check))) {
                    try {
                        changeVersion = Helper.getResourceString(getApplicationContext(), R.string.Check);
                    } catch (Throwable t) {
                        changeVersion = getResources().getString(R.string.Check);
                    }
                    menuItem.setTitle(changeVersion);
                }

                if (menuTitle.equals(getResources().getString(R.string.Comment))) {
                    try {
                        comment = Helper.getResourceString(getApplicationContext(), R.string.Comment);
                    } catch (Throwable t) {
                        comment = getResources().getString(R.string.Comment);
                    }

                    menuItem.setTitle(comment);
                    if (Helper.getSettings("Comment")) {
                        item.setChecked(true);
                    }
                }

                if (menuTitle.equals(getResources().getString(R.string.Contact))) {
                    try {
                        contact = Helper.getResourceString(getApplicationContext(), R.string.Contact);
                    } catch (Throwable t) {
                        contact = getResources().getString(R.string.Contact);
                    }
                    menuItem.setTitle(contact);
                }

                if (menuTitle.equals(getResources().getString(R.string.Customization))) {
                    try {
                        customization = Helper.getResourceString(getApplicationContext(), R.string.Customization);
                    } catch (Throwable t) {
                        customization = getResources().getString(R.string.Customization);
                    }
                    menuItem.setTitle(customization);
                }

                if (menuTitle.equals("Custom Language")) {
                    if (!Helper.getSetting("Language").equals("Instagram")) {
                        item.setChecked(true);
                    }
                }

                if (menuTitle.equals(getResources().getString(R.string.Date))) {
                    try {
                        date = Helper.getResourceString(getApplicationContext(), R.string.Date);
                    } catch (Throwable t) {
                        date = getResources().getString(R.string.Date);
                    }
                    menuItem.setTitle(date);
                        if (!Helper.getSetting("Date").equals("Instagram")) {
                            item.setChecked(true);
                        }
                }

                if (menuTitle.equals(getResources().getString(R.string.Default))) {
                    try {
                        defaultSource = Helper.getResourceString(getApplicationContext(), R.string.Default);
                    } catch (Throwable t) {
                        defaultSource = getResources().getString(R.string.Default);
                    }
                    menuItem.setTitle(defaultSource);
                }

                if (menuTitle.equals(getResources().getString(R.string.Error_log))) {
                    try {
                        errorLog = Helper.getResourceString(getApplicationContext(), R.string.Error_log);
                    } catch (Throwable t) {
                        errorLog = getResources().getString(R.string.Error_log);
                    }
                    menuItem.setTitle(errorLog);
                }

                if (menuTitle.equals(getResources().getString( R.string.File))) {
                    try {
                        file = Helper.getResourceString(getApplicationContext(), R.string.File);
                    } catch (Throwable t) {
                        file = getResources().getString(R.string.File);
                    }
                    menuItem.setTitle(file);
                    if (!Helper.getSetting("File").equals("Instagram")) {
                        item.setChecked(true);
                    }
                }

                if (menuTitle.equals(getResources().getString( R.string.Folder))) {
                    try {
                        folder = Helper.getResourceString(getApplicationContext(), R.string.Folder);
                    } catch (Throwable t) {
                        folder = getResources().getString(R.string.Folder);
                    }
                    menuItem.setTitle(folder);
                    if (Helper.getSettings("Folder")) {
                        item.setChecked(true);
                    }
                }

                if (menuTitle.equals(getResources().getString(R.string.Follow))) {
                    try {
                        follow = Helper.getResourceString(getApplicationContext(), R.string.Follow);
                    } catch (Throwable t) {
                        follow = getResources().getString(R.string.Follow);
                    }
                    menuItem.setTitle(follow);
                }

                if (menuTitle.equals(getResources().getString(R.string.Like))) {
                    try {
                        like = Helper.getResourceString(getApplicationContext(), R.string.Like);
                    } catch (Throwable t) {
                        like = getResources().getString(R.string.Like);
                    }
                    menuItem.setTitle(like);
                    if (Helper.getSettings("Like")) {
                        item.setChecked(true);
                    }
                }

                if (menuTitle.equals(getResources().getString(R.string.Logging))) {
                    try {
                        logging = Helper.getResourceString(getApplicationContext(), R.string.Logging);
                    } catch (Throwable t) {
                        logging = getResources().getString(R.string.Logging);
                    }
                    menuItem.setTitle(logging);
                    if (Helper.getSettings("Log")) {
                        item.setChecked(true);
                    }
                }

                if (menuTitle.equals(getResources().getString(R.string.Hide))) {
                    try {
                        hide = Helper.getResourceString(getApplicationContext(), R.string.Hide);
                    } catch (Throwable t) {
                        hide = getResources().getString(R.string.Hide);
                    }
                    menuItem.setTitle(hide);
                    if (!UiUtils.getActivityVisibleInDrawer(getApplicationContext())) {
                        item.setChecked(true);
                    }
                }

                if (menuTitle.equals(getResources().getString(R.string.Misc))) {
                    try {
                        misc = Helper.getResourceString(getApplicationContext(), R.string.Misc);
                    } catch (Throwable t) {
                        misc = getResources().getString(R.string.Misc);
                    }
                    menuItem.setTitle(misc);
                }

                if (menuTitle.equals(getResources().getString(R.string.NotificationHide))) {
                    try {
                        notifHide = Helper.getResourceString(getApplicationContext(), R.string.NotificationHide);
                    } catch (Throwable t) {
                        notifHide = getResources().getString(R.string.NotificationHide);
                    }
                    menuItem.setTitle(notifHide);
                    if (Helper.getSettings("Notification")) {
                        item.setChecked(true);
                    }
                }

                if (menuTitle.equals(getResources().getString(R.string.the_not_so_big_but_big_button2))) {
                    try {
                        lockFeed = Helper.getResourceString(getApplicationContext(), R.string.the_not_so_big_but_big_button2);
                    } catch (Throwable t) {
                        lockFeed = getResources().getString(R.string.the_not_so_big_but_big_button2);
                    }
                    menuItem.setTitle(lockFeed);
                    if (Helper.getSettings("Lock")) {
                        item.setChecked(true);
                    }
                }

                if (menuTitle.equals("One Tap Video Download")) {
                    if (Helper.getSettings("OneTap")) {
                        item.setChecked(true);
                    }
                }

                if (menuTitle.equals(getResources().getString(R.string.Push))) {
                    try {
                        push = Helper.getResourceString(getApplicationContext(), R.string.Push);
                    } catch (Throwable t) {
                        push = getResources().getString(R.string.Push);
                    }
                    menuItem.setTitle(push);
                    if (Helper.getSettings("Push")) {
                        item.setChecked(true);
                    }
                }

                if (menuTitle.equals(getResources().getString(R.string.Slide))) {
                    try {
                        slide = Helper.getResourceString(getApplicationContext(), R.string.Slide);
                    } catch (Throwable t) {
                        slide = getResources().getString(R.string.Sound);
                    }
                    menuItem.setTitle(slide);
                    if (Helper.getSettings("Slide")) {
                        item.setChecked(true);
                    }
                }

                if (menuTitle.equals(getResources().getString(R.string.Sound))) {
                    try {
                        sound = Helper.getResourceString(getApplicationContext(), R.string.Sound);
                    } catch (Throwable t) {
                        sound = getResources().getString(R.string.Sound);
                    }
                    menuItem.setTitle(sound);
                    if (Helper.getSettings("Sound")) {
                        item.setChecked(true);
                    }
                }

                if (menuTitle.equals(getResources().getString(R.string.Story))) {
                    try {
                        story = Helper.getResourceString(getApplicationContext(), R.string.Story);
                    } catch (Throwable t) {
                        story = getResources().getString(R.string.Story);
                    }
                    menuItem.setTitle(story);
                    if (Helper.getSettings("Story")) {
                        item.setChecked(true);
                    }
                }

                if (menuTitle.equals(getResources().getString(R.string.Suggestion))) {
                    try {
                        suggestion = Helper.getResourceString(getApplicationContext(), R.string.Suggestion);
                    } catch (Throwable t) {
                        suggestion = getResources().getString(R.string.Suggestion);
                    }
                    menuItem.setTitle(suggestion);
                    if (Helper.getSettings("Suggestion")) {
                        item.setChecked(true);
                    }
                }

                if (menuTitle.equals(getResources().getString(R.string.Translations))) {
                    try {
                        translate = Helper.getResourceString(getApplicationContext(), R.string.Translations);
                    } catch (Throwable t) {
                        translate = getResources().getString(R.string.Translations);
                    }
                    menuItem.setTitle(translate);
                }

                try {
                    item.setOnClickListener(null);
                    item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            String clicked = menuTitle;

                            if (clicked.equals(changeOrder)) {
                                Helper.setSetting("Order", Boolean.toString(item.isChecked()));
                            }

                            if (clicked.equals(comment)) {
                                Helper.setSetting("Comment", Boolean.toString(item.isChecked()));
                            }

                            if (clicked.equals("Custom Language")) {
                                if (item.isChecked()) {
                                    openCustomLanguages();
                                } else {
                                    Helper.setSetting("Language", "Instagram");
                                    updateListView();
                                    setupNav();
                                }
                            }

                            if (clicked.equals(date)) {
                                if (item.isChecked() && Helper.getSetting("Date").equals("Instagram")) {
                                    if (Helper.getSettings("Hour")) {
                                        Helper.setSetting("Date", "MM;/dd;/yyyy; ;HH:;mm:;ss:;a;");
                                    } else {
                                        Helper.setSetting("Date","MM;/dd;/yyyy; ;hh:;mm:;ss:;a;");
                                    }



                                } else if (!item.isChecked()) {
                                    Helper.setSetting("Date", "Instagram");
                                }
                            }

                            if (clicked.equals(like)) {
                                Helper.setSetting("Like", Boolean.toString(item.isChecked()));
                            }

                            if (clicked.equals(lockFeed)) {
                                Helper.setSetting("Lock", Boolean.toString(item.isChecked()));
                            }

                            if (clicked.equals(logging)) {
                                Helper.setSetting("Log", Boolean.toString(item.isChecked()));
                            }

                            if (clicked.equals(file)) {
                                System.out.println("File: " +isChecked);
                                if (!isChecked) {
                                    Helper.setSetting("File", "Instagram");
                                } else {
                                    fileDate();
                                }
                            }

                            if (clicked.equals(folder)) {
                                Helper.setSetting("Folder", Boolean.toString(item.isChecked()));
                            }

                            if (clicked.equals(hide)) {
                                if (item.isChecked()) {
                                    UiUtils.setActivityVisibleInDrawer(Main.this, true);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);

                                    String okay;
                                    String warning;

                                    try {
                                        okay = Helper.getResourceString(getApplicationContext(), R.string.Okay);
                                        warning = Helper.getResourceString(getApplicationContext(), R.string.Warning);
                                    } catch (Throwable t) {
                                        okay = getResources().getString(R.string.Okay);
                                        warning = getResources().getString(R.string.Warning);
                                    }

                                    builder.setMessage(warning);
                                    builder.setPositiveButton(okay, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            UiUtils.setActivityVisibleInDrawer(Main.this, false);
                                            item.setChecked(true);
                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                } else {
                                    UiUtils.setActivityVisibleInDrawer(Main.this, true);
                                }
                            }

                            if (clicked.equals("One Tap Video Download")) {
                                if (item.isChecked()) {
                                    Helper.setSetting("OneTap", Boolean.toString(item.isChecked()));
                                } else {
                                    Helper.setSetting("OneTap", Boolean.toString(item.isChecked()));
                                }
                            }

                            if (clicked.equals(notifHide)) {
                                Helper.setSetting("Notification", Boolean.toString(item.isChecked()));
                            }

                            if (clicked.equals(push)) {
                                Helper.setSetting("Push", Boolean.toString(item.isChecked()));
                            }

                            if (clicked.equals(slide)) {
                                Helper.setSetting("Slide", Boolean.toString(item.isChecked()));
                            }

                            if (clicked.equals(sound)) {
                                Helper.setSetting("Sound", Boolean.toString(item.isChecked()));
                            }

                            if (clicked.equals(story)) {
                                Helper.setSetting("Story", Boolean.toString(item.isChecked()));
                            }

                            if (clicked.equals(suggestion)) {
                                Helper.setSetting("Suggestion", Boolean.toString(item.isChecked()));
                            }
                        }
                    });
                } catch (Exception e) {
                }
            }
        }
    }

    void updateListView() {
        String image;
        String profile;
        String recommended;
        String save;
        String updateHooks;
        String video;

        try {
            save = Helper.getResourceString(getApplicationContext(), R.string.Save);
            image = Helper.getResourceString(getApplicationContext(), R.string.Image);
            profile = Helper.getResourceString(getApplicationContext(), R.string.Profile);
            recommended = Helper.getResourceString(getApplicationContext(), R.string.Recommended);
            updateHooks = Helper.getResourceString(getApplicationContext(), R.string.Update);
            video = Helper.getResourceString(getApplicationContext(), R.string.Video);
        } catch (Throwable t) {
            image = "Image Location";
            profile = "Profile Location";
            recommended = "RECOMMENDED";
            save = "SAVE LOCATIONS";
            updateHooks = "UPDATE HOOKS";
            video = "Video Location";
        }

        mAdapter = new Adapter(Main.this);
        mAdapter.addSectionHeaderItem(save);
        mAdapter.addItem(image);
        mAdapter.addItem(profile);
        mAdapter.addItem(video);
        mAdapter.addSectionHeaderItem(updateHooks);
        mAdapter.addItem("GitHub");
        mAdapter.addItem("Pastebin");
        mAdapter.addItem("Alternate Source");
        mAdapter.addSectionHeaderItem(recommended);
        mAdapter.addItem("One Tap Video Download");
        mAdapter.addItem("Zoom For Instagram");

        listView.setAdapter(mAdapter);
    }
}
