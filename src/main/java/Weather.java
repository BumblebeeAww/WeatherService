// Импортируем необходимые библиотеки
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Scanner;

// Создаем класс Weather и определяем две переменные константы
// YANDEX_KEY (полученный нами уникальный ключ от сервиса Яндекс) и MAIN_URL (адрес сервиса запроса)
public class Weather {
    private static final String YANDEX_KEY = "7849545e-2057-46aa-a09a-b796ecebd278";
    private static final String MAIN_URL = "https://api.weather.yandex.ru/v2/forecast";

    // Точка входа в программу (main), создаем объект Scanner для ввода координат и кол-во дней расчета
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите широту: ");
        double lat = scanner.nextDouble();
        System.out.println("Введите долготу: ");
        double lon = scanner.nextDouble();
        System.out.println("Введите количество дней для расчета средней температуры: ");
        int limit = scanner.nextInt();

        // Вызываем метод getWeather с параметрами lat, lon, limit и записываем результат в объект JSON
        // Выводим на экран значение текущей температуры в точке координат
        try {
            String result = getWeather(lat, lon, limit);
            System.out.println("Ответ от сервиса: " + result);

            JSONObject jsonResult = new JSONObject(result);
            double currentTemperature = jsonResult.getJSONObject("fact").getDouble("temp");
            System.out.println("Текущая температура: " + currentTemperature + "°C");

            // Вычисляем среднюю температуру за кол-во дней, которое мы указали
            double averageTemperature = calculateAverageTemperature(jsonResult, limit);
            System.out.println("Средняя температура за " + limit + " дней: " + averageTemperature + "°C");

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            scanner.close();
        }

    }

    // Выполняем запрос к API Яндекс Погода (с обработкой возможных исключений) через URL-адрес запроса,
    // создание экземпляра класса HttpClient и создание объекта HttpRequest,
    // отправка запроса с помощью метода send() и получение ответа методом body()
    private static String getWeather(double lat, double lon, int limit) throws Exception {
        String urlString = "https://api.weather.yandex.ru/v2/forecast?lat=" + lat + "&lon=" + lon;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .header("X-Yandex-API-Key", YANDEX_KEY)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    // Вычисляем среднюю арифметическую температуру за указанное кол-во дней, используя данные JSON-объекта,
    // через работу с массивом прогнозов JSONArray forecasts
    private static double calculateAverageTemperature(JSONObject jsonResult, int limit) {
        JSONArray forecasts = jsonResult.getJSONArray("forecasts");
        double totalTemperature = 0;
        int count = forecasts.length();
        if (limit < forecasts.length()) {
            count = limit;
        }

        for (int i = 0; i < count; i++) {
            JSONObject forecast = forecasts.getJSONObject(i);
            double dayTemperature = forecast.getJSONObject("parts").getJSONObject("day").getDouble("temp_avg");
            totalTemperature += dayTemperature;
        }

        return totalTemperature / count;
    }
}

