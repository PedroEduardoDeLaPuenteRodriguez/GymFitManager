package gymfit;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public class GymFitController {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    @FXML private TextField tfNombre, tfApellido1, tfApellido2, tfIban;
    @FXML private DatePicker dpNacimiento;
    @FXML private ToggleGroup grupoSexo, grupoCuota;
    @FXML private ComboBox<String> cbProvincia, cbClase;
    @FXML private CheckBox ckTaquilla, ckToalla, ckParking;
    @FXML private VBox errorBox;
    @FXML private Label lblErrorTitulo, lblErrorTexto, lblEstado;
    @FXML private TableView<Socio> tablaSocios;

    private final StringConverter<LocalDate> conversor = new StringConverter<>() {
        @Override public String toString(LocalDate d) { return d == null ? "" : FORMATO.format(d); }
        @Override public LocalDate fromString(String t) {
            if (t == null || t.isBlank()) return null;
            try { return LocalDate.parse(t.trim(), FORMATO); } catch (Exception e) { return null; }
        }
    };

    @FXML
    private void initialize() {
        dpNacimiento.setConverter(conversor);
        tablaSocios.setItems(FXCollections.observableArrayList(
                new Socio("Ana Ruiz", "12/03/1999", "Premium"),
                new Socio("Luis Pérez", "21/07/2001", "Básica"),
                new Socio("Marta Gómez", "30/11/1988", "Plus"),
                new Socio("José Díaz", "02/02/2005", "Joven")));
        ocultarError();
    }

    @FXML private void onNuevo() { limpiar(); tfNombre.requestFocus(); }

    @FXML
    private void onGuardar() {
        if (tfNombre.getText().isBlank() || tfApellido1.getText().isBlank()) {
            mostrarError("Faltan datos obligatorios",
                    "Escribe el nombre y el primer apellido del socio para poder guardarlo.");
            (tfNombre.getText().isBlank() ? tfNombre : tfApellido1).requestFocus();
            return;
        }
        LocalDate fecha = conversor.fromString(dpNacimiento.getEditor().getText());
        if (fecha == null || fecha.isAfter(LocalDate.now())) {
            mostrarError("Revisa la fecha de nacimiento",
                    "No hemos podido guardar el socio porque la fecha no tiene un formato válido. "
                  + "Escríbela como dd/mm/aaaa o elígela en el calendario. Ejemplo: 05/11/1999.");
            if (!dpNacimiento.getStyleClass().contains("campo-error")) {
                dpNacimiento.getStyleClass().add("campo-error");
            }
            dpNacimiento.requestFocus();
            return;
        }
        dpNacimiento.setValue(fecha);
        if (cbProvincia.getValue() == null) {
            mostrarError("Elige una provincia",
                    "Selecciona la provincia del socio en la lista desplegable.");
            cbProvincia.requestFocus();
            return;
        }

        String nombreCompleto = (tfNombre.getText().trim() + " " + tfApellido1.getText().trim()
                + " " + tfApellido2.getText().trim()).trim();
        RadioButton cuota = (RadioButton) grupoCuota.getSelectedToggle();
        Socio nuevo = new Socio(nombreCompleto, FORMATO.format(fecha), cuota.getText());
        tablaSocios.getItems().add(0, nuevo);
        tablaSocios.getSelectionModel().select(nuevo);
        ocultarError();
        lblEstado.setText("✔ Socio «" + nombreCompleto + "» guardado correctamente");
        limpiar();
    }

    @FXML
    private void onCorregir() {
        ocultarError();
        dpNacimiento.requestFocus();
    }

    @FXML
    private void onLimpiar() {
        ButtonType limpiar = new ButtonType("Limpiar formulario", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION,
                "Se borrarán todos los datos que has escrito. Esta acción no se puede deshacer.",
                limpiar, cancelar);
        alerta.setTitle("Limpiar formulario");
        alerta.setHeaderText("¿Quieres limpiar el formulario?");
        alerta.showAndWait().ifPresent(r -> { if (r == limpiar) limpiar(); });
    }

    @FXML private void onCancelar() { limpiar(); }
    @FXML private void onSalir() { Platform.exit(); }

    private void mostrarError(String titulo, String texto) {
        lblErrorTitulo.setText(titulo);
        lblErrorTexto.setText(texto);
        errorBox.setVisible(true);
        errorBox.setManaged(true);
    }

    private void ocultarError() {
        errorBox.setVisible(false);
        errorBox.setManaged(false);
        dpNacimiento.getStyleClass().remove("campo-error");
    }

    private void limpiar() {
        tfNombre.clear(); tfApellido1.clear(); tfApellido2.clear(); tfIban.clear();
        dpNacimiento.setValue(null);
        dpNacimiento.getEditor().clear();
        grupoSexo.selectToggle(null);
        grupoCuota.getToggles().get(0).setSelected(true);
        cbProvincia.getSelectionModel().clearSelection();
        cbClase.getSelectionModel().clearSelection();
        ckTaquilla.setSelected(false); ckToalla.setSelected(false); ckParking.setSelected(false);
    }

    public static class Socio {
        private final String nombre, fechaNacimiento, cuota;
        public Socio(String nombre, String fechaNacimiento, String cuota) {
            this.nombre = nombre; this.fechaNacimiento = fechaNacimiento; this.cuota = cuota;
        }
        public String getNombre() { return nombre; }
        public String getFechaNacimiento() { return fechaNacimiento; }
        public String getCuota() { return cuota; }
    }
}
