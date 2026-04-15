package servicios;

import interfaces.InterfazContactoSim;
import modelo.DatosSimulation;
import modelo.DatosSolicitud;
import modelo.Entidad;
import modelo.Punto;
import org.springframework.stereotype.Service;
import utilidades.ApiClient;
import utilidades.ApiException;
import utilidades.api.ResultadosApi;
import utilidades.api.SolicitudApi;
import utilidades.model.ResultsResponse;
import utilidades.model.Solicitud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class ContactoSimService implements InterfazContactoSim {
    private List<Entidad> entidades;
    private List<DatosSolicitud> solicitudes;

    private final SolicitudApi solicitudApi;
    private final ResultadosApi resultadosApi;
    private static final String USUARIO = "irene";

    public ContactoSimService() {
        entidades = new ArrayList<>();
        solicitudes = new ArrayList<>();

        String[] nombres = {"Nombre 1", "Nombre 2", "Nombre 3", "Nombre 4", "Nombre 5"};
        String[] descripciones = {"Descripcion 1", "Descripcion 2", "Descripcion 3", "Descripcion 4", "Descripcion 5"};

        for (int i = 0; i < 5; i++) {
            Entidad e = new Entidad();
            e.setId(i);
            e.setName(nombres[i]);
            e.setDescripcion(descripciones[i]);
            entidades.add(e);
        }
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(":8080");
        System.out.println(apiClient.getBaseUri());
        this.solicitudApi = new SolicitudApi(apiClient);
        this.resultadosApi = new ResultadosApi(apiClient);
    }

    @Override
    public int solicitarSimulation(DatosSolicitud sol) {
        this.solicitudes.add(sol);
        try {
            //Preparar la solicitud para la VM
            Solicitud solicitud = new Solicitud();
            List<Integer> cantidades = new ArrayList<>(sol.getNums().values());
            List<String> nombres = new ArrayList<>();
            for (Entidad e : entidades) {
                nombres.add(e.getName());
            }
            solicitud.setCantidadesIniciales(cantidades);
            solicitud.setNombreEntidades(nombres);

            //Enviar la solicitud a la MV
            solicitudApi.solicitudSolicitarPost(USUARIO, solicitud);

            //Obtener el token que nos asignó la VM
            List<Integer> tokens = solicitudApi.solicitudGetSolicitudesUsuarioGet(USUARIO);
            if (tokens != null && !tokens.isEmpty()) {
                return tokens.get(tokens.size() - 1);
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return new Random().nextInt(10000);
    }

    @Override
    public DatosSimulation descargarDatos(int ticket) {
        try {
            // Llamar al servicio real
            ResultsResponse respuesta = resultadosApi.resultadosPost(USUARIO, ticket);
            if (respuesta != null) {
                String contenidoReal = respuesta.getData();

                if(contenidoReal != null) {
                    System.out.println(contenidoReal);
                    System.out.println(parsearRespuesta(contenidoReal));
                    return parsearRespuesta(contenidoReal);
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return new DatosSimulation();
    }

    private DatosSimulation parsearRespuesta(String respuesta) {
        DatosSimulation ds = new DatosSimulation();
        String[] lineas = respuesta.trim().split("\n");

        if (lineas.length == 0) return ds;

        // Primera línea: ancho del tablero
        ds.setAnchoTablero(Integer.parseInt(lineas[0].trim()));

        // Resto de líneas: tiempo,y,x,color
        Map<Integer, List<Punto>> puntos = new HashMap<>();
        int maxTiempo = 0;

        for (int i = 1; i < lineas.length; i++) {
            String linea = lineas[i].trim();
            if (linea.isEmpty()) continue;

            String[] partes = linea.split(",");
            if (partes.length < 4) continue;

            int tiempo = Integer.parseInt(partes[0].trim());
            int y      = Integer.parseInt(partes[1].trim());
            int x      = Integer.parseInt(partes[2].trim());
            String color = partes[3].trim();

            Punto p = new Punto();
            p.setX(x);
            p.setY(y);
            p.setColor(color);

            puntos.computeIfAbsent(tiempo, k -> new ArrayList<>()).add(p);
            if (tiempo > maxTiempo) maxTiempo = tiempo;
        }

        ds.setPuntos(puntos);
        ds.setMaxSegundos(maxTiempo + 1);
        return ds;
    }

    @Override
    public List<Entidad> getEntities() {
        return entidades;
    }

    @Override
    public boolean isValidEntityId(int id) {
        return entidades.stream().anyMatch(e -> e.getId() == id);
    }
}
