package com.boom.aiobrowser.other;

import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.boom.aiobrowser.APP;
import com.boom.aiobrowser.R;
import com.boom.aiobrowser.tools.AppLogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import kotlin.jvm.internal.Intrinsics;
public class DirectoryProvider extends ContentProvider {
    @NonNull
    public final UriMatcher b = new UriMatcher(-1);
    public int c = 0;
    @NonNull
    public String d = "";

    @NonNull
    public final MatrixCursor a(@NonNull String[] strArr) {
        AppLogs.INSTANCE.dLog("DirectoryProvider","a");
        ArrayList arrayList = new ArrayList(strArr.length);
        for (String str : strArr) {
            str.getClass();
            char c = 65535;
            switch (str.hashCode()) {
                case -2077842241:
                    if (str.equals("has_phone_number")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1391167122:
                    if (str.equals("mimetype")) {
                        c = 1;
                        break;
                    }
                    break;
                case -1274270136:
                    if (str.equals("photo_id")) {
                        c = 2;
                        break;
                    }
                    break;
                case -1097094790:
                    if (str.equals("lookup")) {
                        c = 3;
                        break;
                    }
                    break;
                case -785265135:
                    if (str.equals("raw_contact_id")) {
                        c = 4;
                        break;
                    }
                    break;
                case -300169999:
                    if (str.equals("contact_status")) {
                        c = 5;
                        break;
                    }
                    break;
                case 95356359:
                    if (str.equals("data1")) {
                        c = 6;
                        break;
                    }
                    break;
                case 139876762:
                    if (str.equals("contact_id")) {
                        c = 7;
                        break;
                    }
                    break;
                case 168520786:
                    if (str.equals("display_name_source")) {
                        c = '\b';
                        break;
                    }
                    break;
                case 482193962:
                    if (str.equals("is_user_profile")) {
                        c = '\t';
                        break;
                    }
                    break;
                case 1091239261:
                    if (str.equals("account_name")) {
                        c = '\n';
                        break;
                    }
                    break;
                case 1185544173:
                    if (str.equals("is_primary")) {
                        c = 11;
                        break;
                    }
                    break;
                case 1443195344:
                    if (str.equals("data_id")) {
                        c = '\f';
                        break;
                    }
                    break;
                case 1526397509:
                    if (str.equals("name_raw_contact_id")) {
                        c = '\r';
                        break;
                    }
                    break;
                case 1532803282:
                    if (str.equals("display_name_alt")) {
                        c = 14;
                        break;
                    }
                    break;
                case 1550463001:
                    if (str.equals("deleted")) {
                        c = 15;
                        break;
                    }
                    break;
                case 1563708849:
                    if (str.equals("photo_file_id")) {
                        c = 16;
                        break;
                    }
                    break;
                case 1615086568:
                    if (str.equals("display_name")) {
                        c = 17;
                        break;
                    }
                    break;
                case 1645020905:
                    if (str.equals("is_super_primary")) {
                        c = 18;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                case 2:
                case 5:
                case '\t':
                case 11:
                case 15:
                case 16:
                case 18:
                    arrayList.add(0);
                    break;
                case 1:
                    arrayList.add("vnd.android.cursor.item/email_v2");
                    break;
                case 3:
                case '\n':
                case 14:
                    arrayList.add(this.d);
                    break;
                case 4:
                case 7:
                case '\f':
                case '\r':
                    arrayList.add(1);
                    break;
                case 6:
                    arrayList.add("sobanaliali99@gmail.com");
                    break;
                case '\b':
                    arrayList.add(10);
                    break;
                case 17:
                    Context context = getContext();
                    context.getClass();
                    arrayList.add(APP.instance.getString(R.string.app_name));
                    break;
                default:
                    arrayList.add(null);
                    break;
            }
        }
        MatrixCursor matrixCursor = new MatrixCursor(strArr);
        matrixCursor.addRow(arrayList);
        return matrixCursor;
    }

    @Override // android.content.ContentProvider
    public final int delete(@NonNull Uri uri, @Nullable String str, @Nullable String[] strArr) {
        return 0;
    }

    @Override // android.content.ContentProvider
    @Nullable
    public final String getType(@NonNull Uri uri) {
        return null;
    }

    @Override // android.content.ContentProvider
    @Nullable
    public final Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override // android.content.ContentProvider
    public final boolean onCreate() {
        AppLogs.INSTANCE.dLog("DirectoryProvider","onCreate");
        String str;
        ProviderInfo providerInfo;
        PackageManager.ComponentInfoFlags of;
        Context context = getContext();
        context.getClass();
        int i = context.getApplicationInfo().labelRes;
        if (i == 0) {
            i = R.string.app_name;
        }
        this.c = i;
        this.d = context.getString(i);
        try {
            PackageManager packageManager = context.getPackageManager();
            ComponentName component = new ComponentName(context, DirectoryProvider.class);
            Intrinsics.checkNotNullParameter(packageManager, "<this>");
            Intrinsics.checkNotNullParameter(component, "component");
            if (Build.VERSION.SDK_INT >= 33) {
                of = PackageManager.ComponentInfoFlags.of(0);
                providerInfo = packageManager.getProviderInfo(component, of);
            } else {
                providerInfo = packageManager.getProviderInfo(component, 0);
            }
            Intrinsics1.c(providerInfo);
            str = providerInfo.authority;
        } catch (PackageManager.NameNotFoundException unused) {
            str = context.getPackageName() + ".support";
        }
        UriMatcher uriMatcher = this.b;
        uriMatcher.addURI(str, "directories", 1);
        uriMatcher.addURI(str, "contacts/filter/*", 2);
        uriMatcher.addURI(str, "contacts/lookup/*/entities", 3);
        uriMatcher.addURI(str, "contacts/lookup/*/*/entities", 4);
        return true;
    }


    @Override // android.content.ContentProvider
    @Nullable
    public final Cursor query(@NonNull Uri uri, @Nullable String[] strArr, @Nullable String str, @Nullable String[] strArr2, @Nullable String str2) {
        AppLogs.INSTANCE.dLog("DirectoryProvider","query");
        int lastIndexOf;
        if (strArr == null) {
            return null;
        }
        int match = this.b.match(uri);
        if (match != 1) {
            if (match != 2) {
                if (match != 3 && match != 4) {
                    return null;
                }
                return a(strArr);
            }
            String path = uri.getPath();
            if (path != null && (lastIndexOf = path.lastIndexOf("/contacts/filter/")) >= 0) {
                String lowerCase = path.substring(lastIndexOf + 17).toLowerCase(Locale.getDefault());
                if (!lowerCase.isEmpty()) {
                    String lowerCase2 = this.d.toLowerCase(Locale.getDefault());
                    if (lowerCase.equals(lowerCase2) || Arrays.asList(lowerCase2.split("\\s+")).contains(lowerCase)) {
                        return a(strArr);
                    }
                }
            }
            return null;
        }
        ArrayList arrayList = new ArrayList(strArr.length);
        for (String str3 : strArr) {
            str3.getClass();
            char c = 65535;
            switch (str3.hashCode()) {
                case -1315438423:
                    if (str3.equals("shortcutSupport")) {
                        c = 0;
                        break;
                    }
                    break;
                case -771083909:
                    if (str3.equals("exportSupport")) {
                        c = 1;
                        break;
                    }
                    break;
                case -188162755:
                    if (str3.equals("photoSupport")) {
                        c = 2;
                        break;
                    }
                    break;
                case 94650:
                    if (str3.equals("_id")) {
                        c = 3;
                        break;
                    }
                    break;
                case 865966680:
                    if (str3.equals("accountName")) {
                        c = 4;
                        break;
                    }
                    break;
                case 866168583:
                    if (str3.equals("accountType")) {
                        c = 5;
                        break;
                    }
                    break;
                case 908759025:
                    if (str3.equals("packageName")) {
                        c = 6;
                        break;
                    }
                    break;
                case 1459432611:
                    if (str3.equals("typeResourceId")) {
                        c = 7;
                        break;
                    }
                    break;
                case 1475610435:
                    if (str3.equals("authority")) {
                        c = '\b';
                        break;
                    }
                    break;
                case 1714148973:
                    if (str3.equals("displayName")) {
                        c = '\t';
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                case 1:
                case 2:
                    arrayList.add(0);
                    break;
                case 3:
                    arrayList.add(1);
                    break;
                case 4:
                case 5:
                case '\t':
                    arrayList.add(this.d);
                    break;
                case 6:
                case '\b':
                    arrayList.add("");
                    break;
                case 7:
                    arrayList.add(Integer.valueOf(this.c));
                    break;
                default:
                    arrayList.add(null);
                    break;
            }
        }
        MatrixCursor matrixCursor = new MatrixCursor(strArr);
        matrixCursor.addRow(arrayList);
        return matrixCursor;
    }

    @Override // android.content.ContentProvider
    public final int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String str, @Nullable String[] strArr) {
        AppLogs.INSTANCE.dLog("DirectoryProvider","update");
        return 0;
    }
}