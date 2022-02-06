package stonks;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StonkConnect {	
	@GET("v8/finance/chart/{ticker}?region=US&lang=en-US&includePrePost=false&interval=2m&useYfid=true&range=1d&corsDomain=finance.yahoo.com&.tsrc=finance")
	Call<JsonObject> GET(@Path("ticker") String ticker);
	
	@Headers("Accept: application/json")
	@GET("v1/markets/options/chains?greeks=true")
	Call<JsonObject> getOptions(@Header("Authorization") String auth, @Query("type") String type, @Query("symbol") String stock, @Query("expiration") String expiration);
	
	@Headers("Accept: application/json")
	@GET("v1/markets/options/chains?greeks=true")
	Call<JsonObject> getOptions(@Header("Authorization") String auth, @Query("symbol") String stock, @Query("expiration") String expiration);
	
	@Headers("Accept: application/json")
	@GET("/v1/markets/history")
	Call<JsonObject> getHistory(@Header("Authorization") String auth, @Query("interval") String interval, @Query("symbol") String stock, @Query("start") String start, @Query("end") String end);
	
	@Headers("Accept: application/json")
	@GET("/v1/markets/options/strikes")
	Call<JsonObject> getStrikes(@Header("Authorization") String auth, @Query("symbol") String symbol, @Query("expiration") String expiration);
	
	@Headers("Accept: application/json")
	@GET("/v1/markets/options/expirations?includeAllRoots=true")
	Call<JsonObject> getExprs(@Header("Authorization") String auth, @Query("symbol") String symbol);
	
	@Headers("Accept: application/json")
	@GET("/v1/markets/quotes")
	Call<JsonObject> getQuotes(@Header("Authorization") String auth, @Query("symbols") String symbol, @Query("greeks") String greeks);
}
