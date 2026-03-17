package com.tt1.trabajo;

import modelo.DatosSolicitud;
import modelo.Entidad;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servicios.ContactoSimService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ContactoSimServiceTest {
    private ContactoSimService service;

    @BeforeEach
    void setUp() {
        service = new ContactoSimService();
    }
    @AfterEach
    void tearDown() {
        service = null;
    }

    @Test
    void testGetEntitiesNoVacia() {
        List<Entidad> entidades = service.getEntities();
        assertNotNull(entidades);
        assertFalse(entidades.isEmpty());
    }

    @Test
    void testGetEntitiesNomYDesc() {
        List<Entidad> entidades = service.getEntities();
        for (Entidad e : entidades) {
            assertNotNull(e.getName());
            assertNotNull(e.getDescripcion());
        }
    }

    @Test
    void testSolicitarSimulation() {
        DatosSolicitud ds = new DatosSolicitud(Map.of(0, 5, 1, 3));
        int token = service.solicitarSimulation(ds);
        assertTrue(token >= 0);
    }

    @Test
    void testIsValidEntityIdValido() {
        // El id 0 existe en la lista de entidades
        assertTrue(service.isValidEntityId(0));
    }

    @Test
    void testIsValidEntityIdInvalido() {
        // El id 999 no existe en la lista de entidades
        assertFalse(service.isValidEntityId(999));
    }
}
