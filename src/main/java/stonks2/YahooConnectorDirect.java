package stonks2;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface YahooConnectorDirect {
	@GET
	Call<JsonObject> get(@Url String url);

	@POST
	@Headers("Content-Type: application/json")
	Call<JsonArray> post(@Url String alltickersquery, @Body JsonObject allTickersBody);
}
