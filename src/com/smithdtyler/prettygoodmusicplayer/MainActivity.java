package com.smithdtyler.prettygoodmusicplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity 
{
	private final static int permissionsCode = 1;
	private static final String[] requiredSdkPermissions = new String[]
	{
		Manifest.permission.READ_EXTERNAL_STORAGE, 
	};

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		if (Build.VERSION.SDK_INT < 23) 
		{
			runActualActivity();
			return;
		} 
			
		checkPermission();
	}

	protected void checkPermission() 
	{
		final List<String> missingPermissions = new ArrayList<String>();

		for (final String permission : requiredSdkPermissions) 
		{
			final int result = ContextCompat.checkSelfPermission(this, permission);
			
			if (result == PackageManager.PERMISSION_GRANTED) 
				continue;

			missingPermissions.add(permission);
		}
		
		if (missingPermissions.isEmpty()) 
		{
			runActualActivity();
			return;
		} 
		
		final String[] permissions = missingPermissions
							.toArray(new String[missingPermissions.size()]);
		ActivityCompat.requestPermissions(this, permissions, permissionsCode);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
										   @NonNull int[] grantResults) 
	{
		switch (requestCode) 
		{
			case permissionsCode:
				for (int index = 0; index < permissions.length; index++) 
				{
					if (grantResults[index] == PackageManager.PERMISSION_GRANTED) 
						continue;

					Toast.makeText(this, R.string.not_granted, 
								   Toast.LENGTH_LONG).show();
					finish();
				}

				runActualActivity();
				break;
		}
	}

	public void runActualActivity() 
	{
		Intent intent = new Intent(this, ArtistList.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}
}
