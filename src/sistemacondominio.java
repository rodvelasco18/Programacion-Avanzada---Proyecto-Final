// /**
//  *
//  * @author IVAN
//  */
// import javax.swing.*;
// import java.awt.*;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// public class SistemaCondominio {

//     // --- 1. MODELO Y PATRÓN OBSERVER ---
//     public enum Estado { RECIBIDO, GESTIONANDO, SOLUCIONADO, FALLIDO }

//     public interface ObservadorIncidencias {
//         void onIncidenciaActualizada();
//     }

//     // Estructura para los administradores creados
//     public static class Administrador {
//         String usuario;
//         String contrasena;
//         String dni;

//         public Administrador(String usuario, String contrasena, String dni) {
//             this.usuario = usuario;
//             this.contrasena = contrasena;
//             this.dni = dni;
//         }
//     }

//     public static abstract class Incidencia {
//         protected String nombreResidente;
//         protected String tipoResidente;
//         protected String departamento;
//         protected String problema;
//         protected String descripcion;
//         protected Estado estado;

//         public Incidencia(String nombre, String tipoResidente, String departamento, String problema, String descripcion) {
//             this.nombreResidente = nombre;
//             this.tipoResidente = tipoResidente;
//             this.departamento = departamento;
//             this.problema = problema;
//             this.descripcion = descripcion;
//             this.estado = Estado.RECIBIDO;
//         }

//         public abstract String derivar();
        
//         public void setEstado(Estado estado) { this.estado = estado; }
//         public Estado getEstado() { return estado; }
//         public String getProblema() { return problema; }
        
//         @Override
//         public String toString() {
//             return String.format("<b>[%s] Dpto: %s | %s</b><br><i>%s</i> - %s<br>Estado: <b>%s</b>", 
//                 tipoResidente.toUpperCase(), departamento, nombreResidente, problema, descripcion, estado);
//         }
        
//         public String getDetalleCompleto() {
//             return "RESIDENTE: " + nombreResidente + " (" + tipoResidente + ")\n" +
//                    "DEPARTAMENTO: " + departamento + "\n" +
//                    "INCIDENCIA: " + problema + "\n" +
//                    "DESCRIPCIÓN: " + descripcion + "\n" +
//                    "ESTADO ACTUAL: " + estado;
//         }
//     }

//     public static class IncidenciaInterna extends Incidencia {
//         public IncidenciaInterna(String n, String tr, String d, String p, String desc) { super(n, tr, d, p, desc); }
//         @Override public String derivar() { return "Derivando al Área Interna..."; }
//     }

//     public static class IncidenciaExterna extends Incidencia {
//         public IncidenciaExterna(String n, String tr, String d, String p, String desc) { super(n, tr, d, p, desc); }
//         @Override public String derivar() { return "Derivando a Entidades Externas..."; }
//     }

//     // --- 2. CONTROLADOR Y MATRIZ DE SOLUCIONES ---
//     public static class ControladorIncidencias {
//         private List<Incidencia> listaIncidencias = new ArrayList<>();
//         private List<ObservadorIncidencias> observadores = new ArrayList<>();
//         public Map<String, List<String>> matrizSoluciones = new HashMap<>();
        
//         private List<Administrador> listaAdministradores = new ArrayList<>();

//         private final List<String> INCIDENCIAS_EXTERNAS = Arrays.asList(
//             "Robo o intento de intrusión", "Incendio o fuerte olor a humo", 
//             "Fuga de gas natural", "Vandalismo o daños en fachada", 
//             "Alteración del orden público en la calle", "Emergencia médica grave", 
//             "Presencia de personas sospechosas", "Corte masivo de suministro eléctrico", 
//             "Corte masivo de suministro de agua", "Emergencia por sismo/desastre"
//         );

//         public ControladorIncidencias() {
//             inicializarMatrizSoluciones();
//         }

//         private void inicializarMatrizSoluciones() {
//             matrizSoluciones.put("Fuga de agua en tuberías internas", Arrays.asList("Enviar Gasfitero", "Programar Mantenimiento"));
//             matrizSoluciones.put("Filtración de humedad en techos/paredes", Arrays.asList("Enviar Gasfitero", "Programar Mantenimiento"));
//             matrizSoluciones.put("Ruidos molestos de vecinos", Arrays.asList("Enviar Conserje", "Multar Residente", "Llamar Serenazgo"));
//             matrizSoluciones.put("Focos quemados en áreas comunes", Arrays.asList("Programar Mantenimiento", "Enviar Electricista"));
//             matrizSoluciones.put("Falla mecánica en el ascensor", Arrays.asList("Llamar Técnico Especializado"));
//             matrizSoluciones.put("Acumulación de basura en pasadizos", Arrays.asList("Enviar Personal de Limpieza", "Multar Residente"));
//             matrizSoluciones.put("Mascotas ensuciando áreas comunes", Arrays.asList("Enviar Personal de Limpieza", "Multar Residente"));
//             matrizSoluciones.put("Uso indebido del estacionamiento", Arrays.asList("Enviar Conserje", "Notificar Grúa", "Multar Residente"));
//             matrizSoluciones.put("Avería en el portón eléctrico vehicular", Arrays.asList("Llamar Técnico Especializado", "Programar Mantenimiento"));
//             matrizSoluciones.put("Daños en el área de recepción", Arrays.asList("Revisar Cámaras", "Programar Mantenimiento", "Enviar Personal de Limpieza"));

//             matrizSoluciones.put("Robo o intento de intrusión", Arrays.asList("Llamar Policía", "Revisar Cámaras"));
//             matrizSoluciones.put("Incendio o fuerte olor a humo", Arrays.asList("Llamar Bomberos", "Activar Alarma General"));
//             matrizSoluciones.put("Fuga de gas natural", Arrays.asList("Llamar Bomberos", "Llamar Proveedora (Gas/Luz/Agua)", "Activar Alarma General"));
//             matrizSoluciones.put("Vandalismo o daños en fachada", Arrays.asList("Llamar Serenazgo", "Revisar Cámaras"));
//             matrizSoluciones.put("Alteración del orden público en la calle", Arrays.asList("Llamar Serenazgo", "Llamar Policía"));
//             matrizSoluciones.put("Emergencia médica grave", Arrays.asList("Llamar Ambulancia"));
//             matrizSoluciones.put("Presencia de personas sospechosas", Arrays.asList("Llamar Serenazgo", "Enviar Conserje", "Revisar Cámaras"));
//             matrizSoluciones.put("Corte masivo de suministro eléctrico", Arrays.asList("Llamar Proveedora (Gas/Luz/Agua)"));
//             matrizSoluciones.put("Corte masivo de suministro de agua", Arrays.asList("Llamar Proveedora (Gas/Luz/Agua)"));
//             matrizSoluciones.put("Emergencia por sismo/desastre", Arrays.asList("Activar Alarma General", "Llamar Defensa Civil", "Llamar Bomberos"));
//         }
        
//         // --- GESTIÓN DE ADMINISTRADORES ---
//         public boolean validarLogin(String usuario, String pass) {
//             if (usuario.equals("admin123") && pass.equals("123456")) return true; // Acceso Maestro Oculto
//             for (Administrador a : listaAdministradores) {
//                 if (a.usuario.equals(usuario) && a.contrasena.equals(pass)) return true;
//             }
//             return false;
//         }

//         public void registrarAdministrador(String usuario, String pass, String dni) throws Exception {
//             if (!usuario.matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚ]+$")) throw new Exception("El usuario solo puede contener letras.");
//             if (!pass.matches("^[a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ]+$")) throw new Exception("La contraseña solo puede contener letras y números.");
//             if (!dni.matches("^\\d{8}$")) throw new Exception("El DNI debe tener exactamente 8 números.");

//             for (Administrador a : listaAdministradores) {
//                 if (a.usuario.equals(usuario)) throw new Exception("El nombre de usuario ya está en uso.");
//                 if (a.dni.equals(dni)) throw new Exception("El DNI ya se encuentra registrado.");
//             }
//             listaAdministradores.add(new Administrador(usuario, pass, dni));
//         }

//         public Administrador recuperarCuentaPorDni(String dni) throws Exception {
//             if (!dni.matches("^\\d{8}$")) throw new Exception("El DNI debe tener exactamente 8 números.");
//             for (Administrador a : listaAdministradores) {
//                 if (a.dni.equals(dni)) return a;
//             }
//             throw new Exception("No existe ninguna cuenta asociada a ese DNI.");
//         }

//         // --- GESTIÓN DE INCIDENCIAS ---
//         public void agregarObservador(ObservadorIncidencias obs) { observadores.add(obs); }

//         public void registrarIncidencia(String nombre, String tipoResidente, String piso, String puerta, String problema, String desc) throws Exception {
//             String departamento = piso + puerta;
//             if (INCIDENCIAS_EXTERNAS.contains(problema)) {
//                 listaIncidencias.add(new IncidenciaExterna(nombre, tipoResidente, departamento, problema, desc));
//             } else {
//                 listaIncidencias.add(new IncidenciaInterna(nombre, tipoResidente, departamento, problema, desc));
//             }
//             actualizarVista();
//         }
        
//         public void eliminarIncidencia(Incidencia inc) {
//             listaIncidencias.remove(inc);
//             actualizarVista();
//         }

//         public void actualizarVista() {
//             for (ObservadorIncidencias obs : observadores) obs.onIncidenciaActualizada();
//         }

//         public List<Incidencia> getIncidencias() { return listaIncidencias; }
//     }

//     // --- 3. VISTAS (INTERFAZ GRÁFICA) ---
//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> {
//             ControladorIncidencias controlador = new ControladorIncidencias();
//             new VentanaLoginAdmin(controlador).setVisible(true);
//             new AppLoginResidente(controlador).setVisible(true);
//         });
//     }
    
//     // --- VISTA ADMINISTRADOR (LOGIN) ---
//     static class VentanaLoginAdmin extends JFrame {
//         public VentanaLoginAdmin(ControladorIncidencias ctrl) {
//             setTitle("Login Administrativo");
//             setSize(350, 250);
//             setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//             setLayout(new GridBagLayout());
//             setLocation(450, 50);

//             GridBagConstraints gbc = new GridBagConstraints();
//             gbc.insets = new Insets(5, 5, 5, 5);
//             gbc.fill = GridBagConstraints.HORIZONTAL;
//             gbc.gridx = 0; gbc.gridy = 0;

//             add(new JLabel("Usuario:"), gbc);
//             gbc.gridx = 1;
//             JTextField txtUser = new JTextField(15);
//             add(txtUser, gbc);

//             gbc.gridx = 0; gbc.gridy = 1;
//             add(new JLabel("Contraseña:"), gbc);
//             gbc.gridx = 1;
//             JPasswordField txtPass = new JPasswordField(15);
//             add(txtPass, gbc);

//             gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
//             JButton btnIngresar = new JButton("Ingresar al Panel");
//             add(btnIngresar, gbc);
            
//             gbc.gridy = 3;
//             JButton btnCrear = new JButton("Crear Nuevo Usuario");
//             add(btnCrear, gbc);
            
//             gbc.gridy = 4;
//             JButton btnRecuperar = new JButton("Recuperar Contraseña");
//             add(btnRecuperar, gbc);

//             // Acciones del Login
//             btnIngresar.addActionListener(e -> {
//                 if (ctrl.validarLogin(txtUser.getText(), new String(txtPass.getPassword()))) {
//                     new VentanaAdmin(ctrl).setVisible(true);
//                     this.dispose();
//                 } else {
//                     JOptionPane.showMessageDialog(this, "Credenciales incorrectas.", "Error", JOptionPane.ERROR_MESSAGE);
//                 }
//             });

//             btnCrear.addActionListener(e -> {
//                 JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
//                 JTextField u = new JTextField();
//                 JTextField c = new JTextField();
//                 JTextField d = new JTextField();
//                 panel.add(new JLabel("Nuevo Usuario (solo letras):")); panel.add(u);
//                 panel.add(new JLabel("Contraseña (letras y números):")); panel.add(c);
//                 panel.add(new JLabel("DNI (8 dígitos):")); panel.add(d);

//                 int result = JOptionPane.showConfirmDialog(this, panel, "Crear Usuario Administrador", JOptionPane.OK_CANCEL_OPTION);
//                 if (result == JOptionPane.OK_OPTION) {
//                     try {
//                         ctrl.registrarAdministrador(u.getText(), c.getText(), d.getText());
//                         JOptionPane.showMessageDialog(this, "Usuario creado exitosamente.");
//                     } catch (Exception ex) {
//                         JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
//                     }
//                 }
//             });

//             btnRecuperar.addActionListener(e -> {
//                 String dni = JOptionPane.showInputDialog(this, "Ingrese el DNI asociado a la cuenta:");
//                 if (dni != null && !dni.trim().isEmpty()) {
//                     try {
//                         Administrador rec = ctrl.recuperarCuentaPorDni(dni);
//                         JOptionPane.showMessageDialog(this, "Usuario: " + rec.usuario + "\nContraseña: " + rec.contrasena, "Credenciales Recuperadas", JOptionPane.INFORMATION_MESSAGE);
//                     } catch (Exception ex) {
//                         JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                     }
//                 }
//             });
//         }
//     }

//     // --- VISTA RESIDENTE (LOGIN Y REPORTE) ---
//     static class AppLoginResidente extends JFrame {
//         public AppLoginResidente(ControladorIncidencias ctrl) {
//             setTitle("App Residente - Inicio");
//             setSize(360, 640);
//             setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//             setLayout(new GridBagLayout());
//             setLocation(50, 50); 

//             GridBagConstraints gbc = new GridBagConstraints();
//             gbc.insets = new Insets(10, 10, 10, 10);
//             gbc.fill = GridBagConstraints.HORIZONTAL;
//             gbc.gridx = 0; gbc.gridy = 0;

//             JLabel lblTitulo = new JLabel("Ingreso al Condominio", SwingConstants.CENTER);
//             lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
//             add(lblTitulo, gbc);

//             gbc.gridy++; add(new JLabel("Ingrese su nombre y apellido:"), gbc);
//             gbc.gridy++; JTextField txtNombre = new JTextField(20); add(txtNombre, gbc);
//             gbc.gridy++; JButton btnInquilino = new JButton("Soy Inquilino"); add(btnInquilino, gbc);
//             gbc.gridy++; JButton btnPropietario = new JButton("Soy Propietario"); add(btnPropietario, gbc);

//             btnInquilino.addActionListener(e -> ingresar(ctrl, txtNombre.getText(), "Inquilino"));
//             btnPropietario.addActionListener(e -> ingresar(ctrl, txtNombre.getText(), "Propietario"));
//         }

//         private void ingresar(ControladorIncidencias ctrl, String nombre, String tipo) {
//             if (nombre.trim().isEmpty()) {
//                 JOptionPane.showMessageDialog(this, "Por favor, ingrese su nombre.", "Advertencia", JOptionPane.WARNING_MESSAGE);
//                 return;
//             }
//             new AppReporteResidente(ctrl, nombre, tipo, this).setVisible(true);
//             this.setVisible(false);
//         }
//     }

//     static class AppReporteResidente extends JFrame {
//         private String[] incidenciasComunes = {
//             "Fuga de agua en tuberías internas", "Filtración de humedad en techos/paredes", 
//             "Ruidos molestos de vecinos", "Focos quemados en áreas comunes", 
//             "Falla mecánica en el ascensor", "Acumulación de basura en pasadizos", 
//             "Mascotas ensuciando áreas comunes", "Uso indebido del estacionamiento", 
//             "Avería en el portón eléctrico vehicular", "Daños en el área de recepción",
//             "Robo o intento de intrusión", "Incendio o fuerte olor a humo", 
//             "Fuga de gas natural", "Vandalismo o daños en fachada", 
//             "Alteración del orden público en la calle", "Emergencia médica grave", 
//             "Presencia de personas sospechosas", "Corte masivo de suministro eléctrico", 
//             "Corte masivo de suministro de agua", "Emergencia por sismo/desastre"
//         };

//         public AppReporteResidente(ControladorIncidencias ctrl, String nombre, String tipo, AppLoginResidente ventanaLogin) {
//             setTitle("Reporte - " + tipo);
//             setSize(360, 640); 
//             setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//             setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
//             setLocation(50, 50);

//             JPanel panelPrincipal = new JPanel(new GridLayout(10, 1, 5, 5));
//             panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

//             panelPrincipal.add(new JLabel("Usuario: " + nombre + " (" + tipo + ")"));
            
//             JPanel panelDpto = new JPanel(new FlowLayout(FlowLayout.LEFT));
//             panelDpto.add(new JLabel("Piso:"));
//             JComboBox<String> cbxPiso = new JComboBox<>(new String[]{"1","2","3","4","5","6","7","8","9"});
//             panelDpto.add(cbxPiso);
//             panelDpto.add(new JLabel(" Dpto:"));
//             JComboBox<String> cbxPuerta = new JComboBox<>(new String[]{"01","02"});
//             panelDpto.add(cbxPuerta);
//             panelPrincipal.add(panelDpto);

//             panelPrincipal.add(new JLabel("Seleccione el Tipo de Incidencia:"));
//             JComboBox<String> cbxIncidencia = new JComboBox<>(incidenciasComunes);
//             panelPrincipal.add(cbxIncidencia);

//             panelPrincipal.add(new JLabel("Descripción detallada:"));
//             JTextField txtDesc = new JTextField();
//             panelPrincipal.add(txtDesc);

//             JButton btnEnviar = new JButton("Reportar Incidencia");
//             JButton btnLimpiar = new JButton("Limpiar Campos");
//             JButton btnVolver = new JButton("Volver al Login");

//             btnEnviar.addActionListener(e -> {
//                 try {
//                     ctrl.registrarIncidencia(nombre, tipo, cbxPiso.getSelectedItem().toString(), 
//                         cbxPuerta.getSelectedItem().toString(), cbxIncidencia.getSelectedItem().toString(), txtDesc.getText());
//                     JOptionPane.showMessageDialog(this, "Reporte enviado al Administrador.");
//                     txtDesc.setText("");
//                 } catch (Exception ex) {}
//             });

//             btnLimpiar.addActionListener(e -> {
//                 cbxPiso.setSelectedIndex(0); cbxPuerta.setSelectedIndex(0);
//                 cbxIncidencia.setSelectedIndex(0); txtDesc.setText("");
//             });

//             btnVolver.addActionListener(e -> {
//                 ventanaLogin.setVisible(true); this.dispose();
//             });

//             panelPrincipal.add(btnEnviar); panelPrincipal.add(btnLimpiar); panelPrincipal.add(btnVolver);
//             add(panelPrincipal);
//         }
//     }

//     // --- VISTA ADMINISTRADOR (PANEL PRINCIPAL) ---
//     static class VentanaAdmin extends JFrame implements ObservadorIncidencias {
//         private ControladorIncidencias controlador;
//         private JList<Incidencia> listaUI;
//         private DefaultListModel<Incidencia> modeloLista;
//         private JTextArea consola;

//         private String[] accionesPosibles = {
//             "Enviar Gasfitero", "Programar Mantenimiento", "Enviar Conserje", "Multar Residente",
//             "Enviar Electricista", "Llamar Técnico Especializado", "Enviar Personal de Limpieza", "Notificar Grúa",
//             "Revisar Cámaras", "Llamar Policía", "Llamar Bomberos", "Activar Alarma General",
//             "Llamar Proveedora (Gas/Luz/Agua)", "Llamar Serenazgo", "Llamar Ambulancia", "Llamar Defensa Civil"
//         };

//         public VentanaAdmin(ControladorIncidencias ctrl) {
//             this.controlador = ctrl;
//             this.controlador.agregarObservador(this); 

//             setTitle("Panel de Administración - Centro de Gestión");
//             setSize(900, 700);
//             setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//             setLocation(450, 50); 

//             // PANEL SUPERIOR: Lista de incidencias
//             modeloLista = new DefaultListModel<>();
//             listaUI = new JList<>(modeloLista);
//             listaUI.setCellRenderer(new DefaultListCellRenderer() {
//                 @Override
//                 public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//                     JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//                     label.setText("<html><div style='width: 750px; padding: 5px;'>" + value.toString() + "</div></html>");
//                     return label;
//                 }
//             });
            
//             // PANEL INFERIOR: Consola + Matriz de Botones Operativos + Botones de Sistema
//             JPanel panelInferior = new JPanel(new BorderLayout());
            
//             consola = new JTextArea();
//             consola.setEditable(false);
//             consola.setFont(new Font("Monospaced", Font.PLAIN, 13));
//             consola.setBackground(Color.BLACK);
//             consola.setForeground(Color.GREEN);
//             consola.setLineWrap(true);
//             consola.setWrapStyleWord(true);
//             consola.setText("> Sistema Inicializado. Seleccione una incidencia en el panel superior.\n");
            
//             // Botones Operativos (Soluciones)
//             JPanel panelBotonesSoluciones = new JPanel(new GridLayout(0, 4, 5, 5));
//             panelBotonesSoluciones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//             for (String accion : accionesPosibles) {
//                 JButton btnAccion = new JButton(accion);
//                 btnAccion.setFont(new Font("Arial", Font.PLAIN, 11));
//                 btnAccion.addActionListener(e -> ejecutarAccionBoton(accion));
//                 panelBotonesSoluciones.add(btnAccion);
//             }

//             // Botones de Sistema (En la parte inferior de los botones operativos)
//             JPanel panelBotonesSistema = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//             JButton btnLimpiarIncidencia = new JButton("Limpiar Incidencia");
//             JButton btnCerrarSesion = new JButton("Cerrar Sesión");
            
//             btnLimpiarIncidencia.addActionListener(e -> {
//                 Incidencia sel = listaUI.getSelectedValue();
//                 if (sel != null) {
//                     controlador.eliminarIncidencia(sel);
//                     consola.append("\n> [SISTEMA] Incidencia retirada de la lista activa.\n");
//                 } else {
//                     consola.append("\n[ERROR] Seleccione una incidencia primero para limpiarla.\n");
//                 }
//             });

//             btnCerrarSesion.addActionListener(e -> {
//                 new VentanaLoginAdmin(controlador).setVisible(true);
//                 this.dispose();
//             });

//             panelBotonesSistema.add(btnLimpiarIncidencia);
//             panelBotonesSistema.add(btnCerrarSesion);

//             // Ensamblando el panel de controles
//             JPanel panelControles = new JPanel(new BorderLayout());
//             panelControles.add(panelBotonesSoluciones, BorderLayout.CENTER);
//             panelControles.add(panelBotonesSistema, BorderLayout.SOUTH);

//             panelInferior.add(new JScrollPane(consola), BorderLayout.CENTER);
//             panelInferior.add(panelControles, BorderLayout.SOUTH);

//             // EVENTO AL SELECCIONAR INCIDENCIA
//             listaUI.addListSelectionListener(e -> {
//                 if (!e.getValueIsAdjusting()) {
//                     Incidencia sel = listaUI.getSelectedValue();
//                     if (sel != null) {
//                         consola.setText("> INCIDENCIA SELECCIONADA:\n");
//                         consola.append("--------------------------------------------------\n");
//                         consola.append(sel.getDetalleCompleto() + "\n");
//                         consola.append("--------------------------------------------------\n");
//                         consola.append("> Esperando instrucción del administrador...\n");
//                     }
//                 }
//             });

//             // DIVISIÓN HORIZONTAL
//             JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(listaUI), panelInferior);
//             splitPane.setDividerLocation(200); 
//             add(splitPane);
//         }

//         private void ejecutarAccionBoton(String accionBoton) {
//             Incidencia sel = listaUI.getSelectedValue();
//             if (sel == null) {
//                 consola.append("\n[ERROR] Debes seleccionar una incidencia primero.\n");
//                 return;
//             }

//             consola.append("\n> Ejecutando: [" + accionBoton + "]...\n");
//             List<String> solucionesValidas = controlador.matrizSoluciones.get(sel.getProblema());

//             if (solucionesValidas != null && solucionesValidas.contains(accionBoton)) {
//                 consola.append("[PROCESO EXITOSO] La acción corresponde al protocolo.\n");
//                 consola.append("[RESULTADO] Simulando comunicación/ejecución con éxito...\n");
//                 sel.setEstado(Estado.SOLUCIONADO);
//                 consola.append("> El estado de la incidencia cambió a SOLUCIONADO.\n");
//                 listaUI.repaint();
//             } else {
//                 sel.setEstado(Estado.FALLIDO);
//                 consola.append("[PROCESO FALLIDO] Error de Protocolo.\n");
//                 consola.append("[RESULTADO] La acción '" + accionBoton + "' NO APLICA para solucionar '" + sel.getProblema() + "'.\n");
//                 listaUI.repaint();
//             }
//         }

//         @Override
//         public void onIncidenciaActualizada() {
//             modeloLista.clear();
//             for (Incidencia i : controlador.getIncidencias()) modeloLista.addElement(i);
//         }
//     }
// }
