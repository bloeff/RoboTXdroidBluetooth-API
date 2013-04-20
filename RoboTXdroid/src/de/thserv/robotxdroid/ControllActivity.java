package de.thserv.robotxdroid;

import de.thserv.robotxdroid.R;
import de.thserv.robotxdroid.api.*;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ControllActivity extends Activity {

	private ProgressDialog pDConnecting;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Sensormanager vom System holen
		AnimationView.mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		// Laden der View
		setContentView(R.layout.main);

		// Definition der Buttons für Hoch und Runter
		final Button button1 = (Button) findViewById(R.id.button1);
		button1.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				// Setzen der Wete bei gedrückten Button oder zurücksetzten beim
				// loslassen des Buttons
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					int upVal = ApiEntry.getInstance().GetInIOValue(4);
					if (upVal == 0)
						ApiEntry.getInstance().SetOutPwmValues(4, (short) 512);
					else
						ApiEntry.getInstance().SetOutPwmValues(4, (short) 0);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					ApiEntry.getInstance().SetOutPwmValues(4, (short) 0);
				}
				return false;
			}
		});

		final Button button2 = (Button) findViewById(R.id.button2);
		button2.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				// Setzen der Wete bei gedrückten Button oder zurücksetzten beim
				// loslassen des Buttons
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					int downVal = ApiEntry.getInstance().GetInIOValue(3);
					if (downVal == 0)
						ApiEntry.getInstance().SetOutPwmValues(5, (short) 512);
					else
						ApiEntry.getInstance().SetOutPwmValues(5, (short) 0);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					ApiEntry.getInstance().SetOutPwmValues(5, (short) 0);
				}
				return false;
			}
		});

		// Übergeben der MAC-Adresse an die API
		ApiEntry.getInstance().setMacAdress(
				this.getIntent().getExtras().getString("mac"));
	}

	// Wird auch ausgeführt wenn die App aus dem Pausiert Zustand wiederkommt
	protected void onResume() {
		super.onResume();

		new Thread() {
			public void run() {
				try {
					ControllActivity.this.runOnUiThread(new Runnable() {
						public void run() {

							ControllActivity.this.pDConnecting = ProgressDialog
									.show(ControllActivity.this, "",
											"Verbinden ...");
						}
					});

					// Öffnen der VErbindung zum ROBO TX Controller
					ApiEntry.getInstance().open();

					// Ergebniss des VErbindungsversuchs abwarten
					ControllActivity.this.runOnUiThread(new Runnable() {
						public void run() {
							ControllActivity.this.pDConnecting.dismiss();

							if (ApiEntry.getInstance().getConnectionStatus()
									.equals("INVALID") == true) {
								Toast.makeText(ControllActivity.this,
										"Kein TXController", Toast.LENGTH_SHORT)
										.show();

								ControllActivity.this.finish();

							} else if (ApiEntry.getInstance()
									.getConnectionStatus()
									.equals("NOT CONNECTED") == true) {
								Toast.makeText(ControllActivity.this,
										"Verbindung fehlgeschlagen",
										Toast.LENGTH_SHORT).show();

								ControllActivity.this.finish();

							} else if (ApiEntry.getInstance()
									.getConnectionStatus().equals("CONNECTED") == true) {
								Toast.makeText(ControllActivity.this,
										"Verbindung hergestellt",
										Toast.LENGTH_SHORT).show();
								// Verbindung Hergestellt starten der
								// TransferArea
								ApiEntry.getInstance().startTransferArea();
							}
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	protected void onPause() {
		super.onPause();

		try {
			// App wird Pausiert stoppen der TransferArea und Schließen der
			// Verbindung
			ApiEntry.getInstance().stopTransferArea();
			ApiEntry.getInstance().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
