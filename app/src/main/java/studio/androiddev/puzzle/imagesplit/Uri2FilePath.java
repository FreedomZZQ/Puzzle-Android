package studio.androiddev.puzzle.imagesplit;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

public class Uri2FilePath {
	public String path=null;
	public Uri2FilePath(Uri uri, Activity activity) {

     	String path = "";

     	if (DocumentsContract.isDocumentUri(activity, uri)) {

     	String wholeID = DocumentsContract.getDocumentId(uri);

     	String id = wholeID.split(":")[1];

     	String[] column = { MediaStore.Images.Media.DATA };

     	String sel = MediaStore.Images.Media._ID + "=?";

     	Cursor cursor = activity.getContentResolver().query(

     	MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel,

     	new String[] { id }, null);

     	int columnIndex = cursor.getColumnIndex(column[0]);

     	if (cursor.moveToFirst()) {

     	path = cursor.getString(columnIndex);

     	}

     	cursor.close();

     	} else {

     	String[] projection = { MediaStore.Images.Media.DATA };

     	Cursor cursor = activity.getContentResolver().query(uri,

     	projection, null, null, null);

     	int column_index = cursor

     	.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

     	cursor.moveToFirst();

     	path = cursor.getString(column_index);

     	}



     	this.path=path;

     	 

     	} 
}
