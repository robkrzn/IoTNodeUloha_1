package sk.fri.uniza;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import sk.fri.uniza.api.WeatherStationService;
import sk.fri.uniza.model.WeatherData;

import java.io.IOException;
import java.util.List;

public class IotNode {
    private final Retrofit retrofit;
    private final WeatherStationService weatherStationService;

    public IotNode() {

        retrofit = new Retrofit.Builder()
                // Url adresa kde je umietnená WeatherStation služba
                //.baseUrl("http://localhost:9000/")            //povodna adresa
                .baseUrl("http://ip172-18-0-12-br03952osm4g008o9eag-9000.direct.labs.play-with-docker.com/")
                //.baseUrl("http://212.26.191.77:9000/")        //dusanov server
                // Na konvertovanie JSON objektu na java POJO použijeme
                // Jackson knižnicu
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        // Vytvorenie inštancie komunikačného rozhrania
        weatherStationService = retrofit.create(WeatherStationService.class);

    }

    public WeatherStationService getWeatherStationService() {
        return weatherStationService;
    }

    public double getAverageTemperature(String station,String from, String to){
        double pom=0;
        Call<List<WeatherData>> historia= weatherStationService.getHistoryWeather(station,from,to,List.of("airTemperature"));
        try{
            Response<List<WeatherData>> response = historia.execute();
            if(response.isSuccessful()){
                List<WeatherData> body = response.body();
                for(WeatherData teplota: body){
                    //System.out.println(teplota.getAirTemperature());
                    pom += teplota.getAirTemperature();
                }
                pom = pom / body.size();
                //System.out.println("Velkost: "+body.size());
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return pom;
    }
}
