package com.example.translate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Spinner fromSpinner;
	private Spinner toSpinner;
	private EditText origText;
	private TextView transText;
	private TextView retransText;
	
	private TextWatcher textWatcher;
	private OnItemSelectedListener itemListener;
	
	private Handler guiThread;
	private ExecutorService transThread;
	private Runnable updateTask;
	
	private Future transPending;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initThreading();
		findViews();
		setAdapters();
		setListeners();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void findViews(){
		fromSpinner=(Spinner)findViewById(R.id.from_language);
		toSpinner=(Spinner)findViewById(R.id.to_language);
		origText=(EditText)findViewById(R.id.original_text);
		transText=(TextView)findViewById(R.id.translated_text);
		retransText=(TextView)findViewById(R.id.retranslated_text);
	}
	//初始化下拉列表数据
	private void setAdapters(){
		ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.languages,android.R.layout.simple_spinner_dropdown_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		fromSpinner.setAdapter(adapter);
		toSpinner.setAdapter(adapter);
		fromSpinner.setSelection(0);
		toSpinner.setSelection(2);
		
	}
	private void setListeners(){
		textWatcher=new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				queryUpdate(1000);
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				queryUpdate(200);
			}
		};
		itemListener=new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				queryUpdate(200);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		};
		
		origText.addTextChangedListener(textWatcher);
		fromSpinner.setOnItemSelectedListener(itemListener);
		toSpinner.setOnItemSelectedListener(itemListener);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
	}
	
	private void initThreading(){
		
		guiThread=new Handler();
		transThread=Executors.newSingleThreadExecutor();
		
		updateTask=new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String original=origText.getText().toString().trim();
				
				if(transPending!=null)
					transPending.cancel(true);
				
				if(original.length()==0){
					transText.setText(R.string.empty);
					retransText.setText(R.string.empty);
				}else{
					transText.setText(R.string.translating);
					retransText.setText(R.string.translating);
					
					try{
						TranslateTask translateTask=new TranslateTask(MainActivity.this, original, getLang(fromSpinner), getLang(toSpinner));
						transPending=transThread.submit(translateTask);
					}catch(RejectedExecutionException e){
						transText.setText(R.string.translation_error);
						retransText.setText(R.string.translation_error);
					}
				}
			}
		};
	}
	
	private String getLang(Spinner spinner){
		String result=spinner.getSelectedItem().toString();
		int lparen=result.indexOf('(');
		int rparen=result.indexOf(')');
		result=result.substring(lparen+1, rparen);
		return result;
	}
	
	private void queryUpdate(long delayMillis){
		guiThread.removeCallbacks(updateTask);
		guiThread.postDelayed(updateTask, delayMillis);
	}
	
	public void setTranslated(String text){
		guiSetText(transText, text);
	}

	public void setRetranslated(String text){
		guiSetText(retransText, text);
	}
	//更新gui的内容需要在单独的一个线程里面！
	private void guiSetText(final TextView view,final String text){
		guiThread.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				view.setText(text);
			}
		});
	}
}
