package com.lucasliza.estagiotagview;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
//Lucas Gomes Liza

public class MainActivity extends ActionBarActivity{
	private Button btnConverter;
	private TextView txtValor;
	private Spinner spnMoedas;
	private EditText edtValor;
    private ProgressDialog pDialog;
    
    private static String urlAPI = "https://rate-exchange.appspot.com/currency";
	
	private class GetRate extends AsyncTask<Void, Void, Void> {
		private String valorConvertido = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Inicializar o progressDialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Por favor, aguarde...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
            APICaller apiCaller = new APICaller();
            
			//Separa a parte entre par�nteses da String do spinner, que ser� utilizada para chamar a API Rate Exchange
            String itemSelecionado = spnMoedas.getSelectedItem().toString();
            itemSelecionado = itemSelecionado.substring(itemSelecionado.indexOf('(')+1, itemSelecionado.indexOf(')'));
            
            //Criar a Lista com os par�metros para a chamada da API Rate Exchange
            List<NameValuePair> params = new ArrayList<NameValuePair>(3);
            params.add(new BasicNameValuePair("from", itemSelecionado));
            params.add(new BasicNameValuePair("to", "BRL"));
            params.add(new BasicNameValuePair("q", edtValor.getText().toString()));
            
            //Realiza chamada e devolve JSON de resposta
            String jsonStr = apiCaller.sendRequest(urlAPI, params);
            
            if (jsonStr != null) {
                try {
                		JSONObject jsonObj = new JSONObject(jsonStr);
                		valorConvertido = jsonObj.getString("v"); //Acessa o o campo de nome 'v' e pega seu valor, no caso
                											      //o resultado da convers�o
                		valorConvertido = String.format("%.02f", Float.parseFloat(valorConvertido));
                    }    
                catch (JSONException e) {
                		//caso o servi�o do  Rate Exchange estivesse indispon�vel retornava uma p�gina html, causando erro de
                	    //formata��o do JSON
                		valorConvertido = "";
                }
            } else {
            	valorConvertido = "";
            }
 
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //Fecha o progress dialog, pois todo o trabalho j� foi feito
            if (pDialog.isShowing())
                pDialog.dismiss();
            
            // Mostrar para usu�rio o valor convertido
            // Se valorConvertido � "" quer dizer que n�o foi poss�vel calcular
            if (!valorConvertido.equals(""))
            	txtValor.setText(valorConvertido+" reais");
            else
            	txtValor.setText("Servi�o [Rate Exchange] temporariamente indispon�vel.");
        }
 
    }
 
	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (edtValor.getText().toString().equals(""))
				Toast.makeText(getApplicationContext(), 
						"Insira um valor antes!",
	                    Toast.LENGTH_SHORT).show();
			else
				new GetRate().execute();
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtValor = (TextView)findViewById(R.id.txtValor);
    	btnConverter = (Button)findViewById(R.id.btnConverter);
    	btnConverter.setOnClickListener(onClickListener);
    	spnMoedas = (Spinner) findViewById(R.id.spnMoedas);
    	edtValor = (EditText) findViewById(R.id.edtValor);
    	//preencher o spinner com os dados 
    	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, 
                R.array._moedas, 
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMoedas.setAdapter(adapter);
    }

}
