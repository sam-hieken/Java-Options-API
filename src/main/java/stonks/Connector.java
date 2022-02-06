package stonks;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Connector implements Closeable {
	public final PrintWriter fout;
	public final OkHttpClient ok;
	public final Retrofit rfit;
	public final StonkConnect conn;
	
	/**
	 * Inaccessible outside object
	 */
	private final String[] tokens;
	
	private byte nextHeader = 0;
	
	public String nextHeader() {
		nextHeader = nextN(nextHeader);
		
		System.out.println(nextHeader);
		return "Bearer " + tokens[nextHeader];
	}
	
	private byte nextN(byte n) {
		n++;
		if(n > tokens.length - 1) n = 0;
		return n;
	}
	
	public Connector(String...tokens) {
		this.fout = new PrintWriter(System.err);
		this.ok = new OkHttpClient();
		this.rfit = new Retrofit.Builder().baseUrl("https://sandbox.tradier.com")
				.client(ok)
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		this.conn = rfit.create(StonkConnect.class);
		this.tokens = tokens;
		
	}
	
	public Connector(File log, String...tokens) throws FileNotFoundException {
		this.fout = new PrintWriter(new FileOutputStream(log, true));
		
		Interceptor interceptor = new Interceptor() {

			public Response intercept(Chain chain) throws IOException {
				Response r = chain.proceed(chain.request());
				//if((r.code() / 100) != 2) {
					fout.println(chain.request());
					fout.println(r);
				//}
				return r;
			}
			
		};
		
		this.ok = new OkHttpClient.Builder().addInterceptor(interceptor).build();
		
		this.rfit = new Retrofit.Builder().baseUrl("https://sandbox.tradier.com")
			.client(ok)
			.addConverterFactory(GsonConverterFactory.create())
			.build();
		this.conn = rfit.create(StonkConnect.class);
		this.tokens = tokens;
		
	}

	public void close() throws IOException {
		ok.connectionPool().evictAll();
		ok.dispatcher().executorService().shutdown();
		fout.close();
	}
}
