/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package sistemacondominio;

/**
 *
 * @author IVAN
 */
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SistemaCondominio {

    // --- 1. MODELO Y PATRÓN OBSERVER ---
    public enum Estado { PENDIENTE, EN_CURSO, COMPLETADO, FALLIDO }

    public interface ObservadorIncidencias {
        void onIncidenciaActualizada();
    }

    public static class UsuarioAdmin {
        private String usuario;
        private String password;
        private String dni;

        public UsuarioAdmin(String u, String p, String d) { usuario = u; password = p; dni = d; }
        public String getUsuario() { return usuario; }
        public String getPassword() { return password; }
        public String getDni() { return dni; }
    }

    public static abstract class Incidencia {
        protected String nombreResidente;
        protected String tipoResidente;
        protected String departamento;
        protected String problema;
        protected String descripcion;
        protected Estado estado;
        protected boolean resolucionEnMarcha; // Evita duplicar el proceso de tiempo

        public Incidencia(String nombre, String tipoResidente, String departamento, String problema, String descripcion) {
            this.nombreResidente = nombre;
            this.tipoResidente = tipoResidente;
            this.departamento = departamento;
            this.problema = problema;
            this.descripcion = descripcion;
            this.estado = Estado.PENDIENTE;
            this.resolucionEnMarcha = false;
        }

        public void setEstado(Estado estado) { this.estado = estado; }
        public Estado getEstado() { return estado; }
        public String getProblema() { return problema; }
        public String getDepartamento() { return departamento; }
        public boolean isResolucionEnMarcha() { return resolucionEnMarcha; }
        public void setResolucionEnMarcha(boolean estado) { this.resolucionEnMarcha = estado; }
        
        public String getDetalleCompleto() {
            return "RESIDENTE: " + nombreResidente + " (" + tipoResidente + ")\n" +
                   "DEPARTAMENTO: " + departamento + "\nINCIDENCIA: " + problema + "\n" +
                   "DESCRIPCIÓN: " + descripcion + "\nESTADO ACTUAL: " + estado;
        }
    }

    public static class IncidenciaInterna extends Incidencia {
        public IncidenciaInterna(String n, String tr, String d, String p, String desc) { super(n, tr, d, p, desc); }
    }

    public static class IncidenciaExterna extends Incidencia {
        public IncidenciaExterna(String n, String tr, String d, String p, String desc) { super(n, tr, d, p, desc); }
    }

    // --- 2. CONTROLADOR ---
    public static class ControladorSistema {
        private List<Incidencia> listaIncidencias = new ArrayList<>();
        private List<UsuarioAdmin> listaAdministradores = new ArrayList<>();
        private List<ObservadorIncidencias> observadores = new ArrayList<>();
        public Map<String, List<String>> matrizSoluciones = new HashMap<>();

        private final List<String> INCIDENCIAS_EXTERNAS = Arrays.asList(
            "Robo o intento de intrusión", "Incendio o fuerte olor a humo", 
            "Fuga de gas natural", "Vandalismo o daños en fachada", 
            "Alteración del orden público en la calle", "Emergencia médica grave", 
            "Presencia de personas sospechosas", "Corte masivo de suministro eléctrico", 
            "Corte masivo de suministro de agua", "Emergencia por sismo/desastre"
        );

        public ControladorSistema() {
            matrizSoluciones.put("Fuga de agua en tuberías internas", Arrays.asList("Enviar Gásfiter", "Programar Mantenimiento"));
            matrizSoluciones.put("Filtración de humedad en techos/paredes", Arrays.asList("Enviar Gásfiter", "Programar Mantenimiento"));
            matrizSoluciones.put("Ruidos molestos de vecinos", Arrays.asList("Enviar Conserje", "Multar Residente", "Llamar Serenazgo"));
            matrizSoluciones.put("Focos quemados en áreas comunes", Arrays.asList("Programar Mantenimiento", "Enviar Electricista"));
            matrizSoluciones.put("Falla mecánica en el ascensor", Arrays.asList("Llamar Técnico Especializado"));
            matrizSoluciones.put("Acumulación de basura en pasadizos", Arrays.asList("Enviar Personal de Limpieza", "Multar Residente"));
            matrizSoluciones.put("Mascotas ensuciando áreas comunes", Arrays.asList("Enviar Personal de Limpieza", "Multar Residente"));
            matrizSoluciones.put("Uso indebido del estacionamiento", Arrays.asList("Enviar Conserje", "Notificar Grúa", "Multar Residente"));
            matrizSoluciones.put("Avería en el portón eléctrico vehicular", Arrays.asList("Llamar Técnico Especializado", "Programar Mantenimiento"));
            matrizSoluciones.put("Daños en el área de recepción", Arrays.asList("Revisar Cámaras", "Programar Mantenimiento", "Enviar Personal de Limpieza"));

            matrizSoluciones.put("Robo o intento de intrusión", Arrays.asList("Llamar Policía", "Revisar Cámaras"));
            matrizSoluciones.put("Incendio o fuerte olor a humo", Arrays.asList("Llamar Bomberos", "Activar Alarma General"));
            matrizSoluciones.put("Fuga de gas natural", Arrays.asList("Llamar Bomberos", "Llamar Proveedora (Gas/Luz/Agua)", "Activar Alarma General"));
            matrizSoluciones.put("Vandalismo o daños en fachada", Arrays.asList("Llamar Serenazgo", "Revisar Cámaras"));
            matrizSoluciones.put("Alteración del orden público en la calle", Arrays.asList("Llamar Serenazgo", "Llamar Policía"));
            matrizSoluciones.put("Emergencia médica grave", Arrays.asList("Llamar Ambulancia"));
            matrizSoluciones.put("Presencia de personas sospechosas", Arrays.asList("Llamar Serenazgo", "Enviar Conserje", "Revisar Cámaras"));
            matrizSoluciones.put("Corte masivo de suministro eléctrico", Arrays.asList("Llamar Proveedora (Gas/Luz/Agua)"));
            matrizSoluciones.put("Corte masivo de suministro de agua", Arrays.asList("Llamar Proveedora (Gas/Luz/Agua)"));
            matrizSoluciones.put("Emergencia por sismo/desastre", Arrays.asList("Activar Alarma General", "Llamar Defensa Civil", "Llamar Bomberos"));
        }

        public void crearUsuarioAdmin(String usr, String pwd, String dni) throws Exception {
            if (!usr.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ]+$")) throw new Exception("El usuario solo puede contener letras.");
            if (!pwd.matches("^[a-zA-Z0-9]+$")) throw new Exception("La contraseña solo puede contener letras y números.");
            if (!dni.matches("^\\d{8}$")) throw new Exception("El DNI debe contener exactamente 8 números.");
            for (UsuarioAdmin u : listaAdministradores) {
                if (u.getUsuario().equalsIgnoreCase(usr)) throw new Exception("El usuario ya existe.");
                if (u.getDni().equals(dni)) throw new Exception("El DNI ya se encuentra registrado.");
            }
            listaAdministradores.add(new UsuarioAdmin(usr, pwd, dni));
        }

        public boolean validarAccesoAdmin(String usr, String pwd) {
            if (usr.equals("admin123") && pwd.equals("123456")) return true; 
            for (UsuarioAdmin u : listaAdministradores) {
                if (u.getUsuario().equals(usr) && u.getPassword().equals(pwd)) return true;
            }
            return false;
        }

        public UsuarioAdmin recuperarCredenciales(String dni) {
            for (UsuarioAdmin u : listaAdministradores) if (u.getDni().equals(dni)) return u;
            return null;
        }

        public void agregarObservador(ObservadorIncidencias obs) { observadores.add(obs); }

        public void registrarIncidencia(String nombre, String tipoResidente, String piso, String puerta, String problema, String desc) {
            String departamento = piso + puerta;
            if (INCIDENCIAS_EXTERNAS.contains(problema)) {
                listaIncidencias.add(new IncidenciaExterna(nombre, tipoResidente, departamento, problema, desc));
            } else {
                listaIncidencias.add(new IncidenciaInterna(nombre, tipoResidente, departamento, problema, desc));
            }
            actualizarVista();
        }
        
        public void eliminarIncidencia(Incidencia incidencia) {
            listaIncidencias.remove(incidencia);
            actualizarVista();
        }

        public void actualizarVista() {
            for (ObservadorIncidencias obs : observadores) obs.onIncidenciaActualizada();
        }

        public List<Incidencia> getIncidencias() { return listaIncidencias; }
    }

    // --- 3. COMPONENTE VISUAL: PANEL CON IMAGEN ---
    static class PanelFondo extends JPanel {
        private Image imagenFondo;
        public PanelFondo(String rutaImagen) {
            File f = new File(rutaImagen);
            if (f.exists()) imagenFondo = new ImageIcon(rutaImagen).getImage();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imagenFondo != null) {
                g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            } else {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, new Color(70, 130, 180), getWidth(), getHeight(), new Color(25, 25, 112)));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    // --- 4. VISTAS (INTERFAZ GRÁFICA) ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ControladorSistema controlador = new ControladorSistema();
            new VentanaLoginAdmin(controlador).setVisible(true);
            new AppLoginResidente(controlador).setVisible(true);
        });
    }

    static class VentanaLoginAdmin extends JFrame {
        public VentanaLoginAdmin(ControladorSistema ctrl) {
            setTitle("Login - Administrador");
            setSize(350, 220);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new GridLayout(4, 1, 5, 5));
            setLocation(450, 200);

            JPanel panelInputs = new JPanel(new GridLayout(2, 2, 5, 5));
            panelInputs.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
            panelInputs.add(new JLabel("Usuario:"));
            JTextField txtUser = new JTextField();
            panelInputs.add(txtUser);
            panelInputs.add(new JLabel("Contraseña:"));
            JPasswordField txtPass = new JPasswordField();
            panelInputs.add(txtPass);

            JButton btnIngresar = new JButton("Ingresar");
            JButton btnCrear = new JButton("Crear Usuario");
            JButton btnRecuperar = new JButton("Recuperar Credenciales");

            btnIngresar.addActionListener(e -> {
                if (ctrl.validarAccesoAdmin(txtUser.getText(), new String(txtPass.getPassword()))) {
                    new VentanaAdmin(ctrl).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Credenciales incorrectas.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            btnCrear.addActionListener(e -> {
                JTextField nUser = new JTextField(); JTextField nPass = new JTextField(); JTextField nDni = new JTextField();
                Object[] campos = {"Nuevo Usuario (Solo letras):", nUser, "Contraseña (Letras y números):", nPass, "DNI (8 dígitos):", nDni};
                if (JOptionPane.showConfirmDialog(this, campos, "Crear Nuevo Administrador", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                    try {
                        ctrl.crearUsuarioAdmin(nUser.getText(), nPass.getText(), nDni.getText());
                        JOptionPane.showMessageDialog(this, "Usuario creado exitosamente.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            btnRecuperar.addActionListener(e -> {
                String dniStr = JOptionPane.showInputDialog(this, "Ingrese su número de DNI de 8 dígitos:");
                if (dniStr != null) {
                    UsuarioAdmin rec = ctrl.recuperarCredenciales(dniStr);
                    if (rec != null) JOptionPane.showMessageDialog(this, "Usuario: " + rec.getUsuario() + "\nContraseña: " + rec.getPassword(), "Recuperado", JOptionPane.INFORMATION_MESSAGE);
                    else JOptionPane.showMessageDialog(this, "No se encontró usuario con ese DNI.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            JPanel pnlIngreso = new JPanel(new FlowLayout()); pnlIngreso.add(btnIngresar);
            JPanel pnlOpciones = new JPanel(new FlowLayout()); pnlOpciones.add(btnCrear); pnlOpciones.add(btnRecuperar);

            add(new JLabel("ACCESO AL CENTRO DE GESTIÓN KANBAN", SwingConstants.CENTER));
            add(panelInputs); add(pnlIngreso); add(pnlOpciones);
        }
    }

    static class AppLoginResidente extends JFrame {
        public AppLoginResidente(ControladorSistema ctrl) {
            setTitle("App Residente");
            setSize(380, 640);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new GridBagLayout());
            setLocation(50, 50); 

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0; gbc.gridy = 0;

            JLabel lblTitulo = new JLabel("Ingreso al Condominio", SwingConstants.CENTER);
            lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
            add(lblTitulo, gbc);

            gbc.gridy++; add(new JLabel("Ingrese su nombre y apellido:"), gbc);
            gbc.gridy++; JTextField txtNombre = new JTextField(20); add(txtNombre, gbc);
            gbc.gridy++; JButton btnInquilino = new JButton("Soy Inquilino"); add(btnInquilino, gbc);
            gbc.gridy++; JButton btnPropietario = new JButton("Soy Propietario"); add(btnPropietario, gbc);

            btnInquilino.addActionListener(e -> ingresar(ctrl, txtNombre.getText(), "Inquilino"));
            btnPropietario.addActionListener(e -> ingresar(ctrl, txtNombre.getText(), "Propietario"));
        }

        private void ingresar(ControladorSistema ctrl, String nombre, String tipo) {
            if (nombre.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese su nombre.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            new AppReporteResidente(ctrl, nombre, tipo, this).setVisible(true);
            this.setVisible(false);
        }
    }

    static class AppReporteResidente extends JFrame {
        private String[] incidenciasComunes = {
            "Fuga de agua en tuberías internas", "Filtración de humedad en techos/paredes", 
            "Ruidos molestos de vecinos", "Focos quemados en áreas comunes", 
            "Falla mecánica en el ascensor", "Acumulación de basura en pasadizos", 
            "Mascotas ensuciando áreas comunes", "Uso indebido del estacionamiento", 
            "Avería en el portón eléctrico vehicular", "Daños en el área de recepción",
            "Robo o intento de intrusión", "Incendio o fuerte olor a humo", 
            "Fuga de gas natural", "Vandalismo o daños en fachada", 
            "Alteración del orden público en la calle", "Emergencia médica grave", 
            "Presencia de personas sospechosas", "Corte masivo de suministro eléctrico", 
            "Corte masivo de suministro de agua", "Emergencia por sismo/desastre"
        };

        public AppReporteResidente(ControladorSistema ctrl, String nombre, String tipo, AppLoginResidente ventanaLogin) {
            setTitle("Reporte - " + tipo);
            setSize(380, 640); 
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocation(50, 50);

            JPanel panelPrincipal = new JPanel(new GridLayout(10, 1, 5, 5));
            panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            panelPrincipal.add(new JLabel("Usuario: " + nombre + " (" + tipo + ")"));
            
            JPanel panelDpto = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panelDpto.add(new JLabel("Piso:"));
            JComboBox<String> cbxPiso = new JComboBox<>(new String[]{"1","2","3","4","5","6","7","8","9"});
            panelDpto.add(cbxPiso);
            panelDpto.add(new JLabel(" Dpto:"));
            JComboBox<String> cbxPuerta = new JComboBox<>(new String[]{"01","02"});
            panelDpto.add(cbxPuerta);
            panelPrincipal.add(panelDpto);

            panelPrincipal.add(new JLabel("Seleccione el Tipo de Incidencia:"));
            JComboBox<String> cbxIncidencia = new JComboBox<>(incidenciasComunes);
            panelPrincipal.add(cbxIncidencia);

            panelPrincipal.add(new JLabel("Descripción detallada:"));
            JTextField txtDesc = new JTextField();
            panelPrincipal.add(txtDesc);

            JButton btnEnviar = new JButton("Reportar Incidencia");
            JButton btnLimpiar = new JButton("Limpiar Campos");
            JButton btnVolver = new JButton("Volver al Login");

            btnEnviar.addActionListener(e -> {
                ctrl.registrarIncidencia(nombre, tipo, cbxPiso.getSelectedItem().toString(), 
                    cbxPuerta.getSelectedItem().toString(), cbxIncidencia.getSelectedItem().toString(), txtDesc.getText());
                JOptionPane.showMessageDialog(this, "Reporte enviado al Administrador.");
                txtDesc.setText("");
            });

            btnLimpiar.addActionListener(e -> {
                cbxPiso.setSelectedIndex(0); cbxPuerta.setSelectedIndex(0);
                cbxIncidencia.setSelectedIndex(0); txtDesc.setText("");
            });

            btnVolver.addActionListener(e -> { ventanaLogin.setVisible(true); this.dispose(); });

            panelPrincipal.add(btnEnviar); panelPrincipal.add(btnLimpiar); panelPrincipal.add(btnVolver);
            JScrollPane scrollResidente = new JScrollPane(panelPrincipal);
            scrollResidente.setBorder(null);
            add(scrollResidente);
        }
    }

    static class VentanaAdmin extends JFrame implements ObservadorIncidencias {
        private ControladorSistema controlador;
        private JTextArea consola;
        private Incidencia incidenciaSeleccionada = null;
        private JPanel tarjetaVisualSeleccionada = null;

        private JPanel panelPendiente, panelEnCurso, panelCompletado;

        private String[] accionesPosibles = {
            "Tomar Caso (Mover a En Curso)", "Enviar Gásfiter", "Programar Mantenimiento", 
            "Enviar Conserje", "Multar Residente", "Enviar Electricista", 
            "Llamar Técnico Especializado", "Enviar Personal de Limpieza", "Notificar Grúa",
            "Revisar Cámaras", "Llamar Policía", "Llamar Bomberos", "Activar Alarma General",
            "Llamar Proveedora (Gas/Luz/Agua)", "Llamar Serenazgo", "Llamar Ambulancia", "Llamar Defensa Civil"
        };

        public VentanaAdmin(ControladorSistema ctrl) {
            this.controlador = ctrl;
            this.controlador.agregarObservador(this); 

            setTitle("Tablero de Gestión de Incidencias");
            setSize(1100, 750);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            PanelFondo fondoTablero = new PanelFondo("ui.jpeg");
            fondoTablero.setLayout(new BorderLayout());

            JPanel panelTitulos = new JPanel(new GridLayout(1, 3, 15, 0));
            panelTitulos.setOpaque(false);
            panelTitulos.setBorder(new EmptyBorder(10, 15, 5, 15));
            panelTitulos.add(crearEtiquetaTitulo("Pendiente"));
            panelTitulos.add(crearEtiquetaTitulo("En Curso"));
            panelTitulos.add(crearEtiquetaTitulo("Completado"));
            fondoTablero.add(panelTitulos, BorderLayout.NORTH);

            JPanel panelColumnas = new JPanel(new GridLayout(1, 3, 15, 0));
            panelColumnas.setOpaque(false);
            panelColumnas.setBorder(new EmptyBorder(0, 15, 10, 15));

            panelPendiente = crearColumnaListado();
            panelEnCurso = crearColumnaListado();
            panelCompletado = crearColumnaListado();

            panelColumnas.add(crearScrollTransparente(panelPendiente));
            panelColumnas.add(crearScrollTransparente(panelEnCurso));
            panelColumnas.add(crearScrollTransparente(panelCompletado));
            fondoTablero.add(panelColumnas, BorderLayout.CENTER);

            JPanel panelInferior = new JPanel(new BorderLayout());
            
            consola = new JTextArea(8, 0);
            consola.setEditable(false);
            consola.setFont(new Font("Monospaced", Font.PLAIN, 12));
            consola.setBackground(new Color(20, 20, 20));
            consola.setForeground(new Color(0, 255, 0));
            consola.setText("> Sistema Kanban Iniciado.\n> Seleccione una tarjeta para gestionar...\n");
            
            JPanel panelBotonesAcciones = new JPanel(new GridLayout(0, 4, 5, 5));
            panelBotonesAcciones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            for (String accion : accionesPosibles) {
                JButton btn = new JButton(accion);
                btn.setFont(new Font("Arial", Font.PLAIN, 11));
                btn.addActionListener(e -> ejecutarAccionBoton(accion));
                panelBotonesAcciones.add(btn);
            }

            JPanel panelControlesExtra = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnLimpiar = new JButton("Eliminar Tarjeta");
            JButton btnCerrar = new JButton("Cerrar Sesión");
            
            btnLimpiar.addActionListener(e -> {
                if (incidenciaSeleccionada != null) {
                    controlador.eliminarIncidencia(incidenciaSeleccionada);
                    incidenciaSeleccionada = null; tarjetaVisualSeleccionada = null;
                    consola.append("> Tarjeta eliminada del tablero.\n");
                }
            });
            btnCerrar.addActionListener(e -> { new VentanaLoginAdmin(ctrl).setVisible(true); this.dispose(); }); 
            
            panelControlesExtra.add(btnLimpiar); panelControlesExtra.add(btnCerrar);

            JPanel panelSurBotones = new JPanel(new BorderLayout());
            panelSurBotones.add(panelBotonesAcciones, BorderLayout.CENTER);
            panelSurBotones.add(panelControlesExtra, BorderLayout.SOUTH);

            panelInferior.add(new JScrollPane(consola), BorderLayout.CENTER);
            panelInferior.add(panelSurBotones, BorderLayout.SOUTH);

            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, fondoTablero, panelInferior);
            splitPane.setDividerLocation(450); 
            add(splitPane);
            cargarTarjetas();
        }

        private JLabel crearEtiquetaTitulo(String texto) {
            JLabel lbl = new JLabel(texto);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
            lbl.setForeground(Color.WHITE); 
            return lbl;
        }

        private JPanel crearColumnaListado() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setOpaque(false);
            return panel;
        }

        private JScrollPane crearScrollTransparente(JPanel contenido) {
            JScrollPane scroll = new JScrollPane(contenido);
            scroll.setOpaque(false); scroll.getViewport().setOpaque(false); scroll.setBorder(null);
            scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            return scroll;
        }

        private Border obtenerBordeNormal() {
            return BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0),
                BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        }

        private Border obtenerBordeResaltado() {
            return BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0),
                BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE, 2, true), BorderFactory.createEmptyBorder(9, 9, 9, 9)));
        }

        private void cargarTarjetas() {
            panelPendiente.removeAll(); panelEnCurso.removeAll(); panelCompletado.removeAll();
            tarjetaVisualSeleccionada = null; 

            for (Incidencia inc : controlador.getIncidencias()) {
                JPanel tarjeta = crearTarjetaVisual(inc);
                if (inc.getEstado() == Estado.PENDIENTE || inc.getEstado() == Estado.FALLIDO) panelPendiente.add(tarjeta);
                else if (inc.getEstado() == Estado.EN_CURSO) panelEnCurso.add(tarjeta);
                else if (inc.getEstado() == Estado.COMPLETADO) panelCompletado.add(tarjeta);
            }
            panelPendiente.revalidate(); panelPendiente.repaint();
            panelEnCurso.revalidate(); panelEnCurso.repaint();
            panelCompletado.revalidate(); panelCompletado.repaint();
        }

        private JPanel crearTarjetaVisual(Incidencia inc) {
            JPanel card = new JPanel(new BorderLayout(5, 5));
            card.setBackground(Color.WHITE); card.setMaximumSize(new Dimension(350, 100));
            card.setBorder(obtenerBordeNormal());

            JLabel lblTag = new JLabel(" Dpto: " + inc.getDepartamento() + " ");
            lblTag.setOpaque(true); lblTag.setBackground(new Color(30, 144, 255));
            lblTag.setForeground(Color.WHITE); lblTag.setFont(new Font("SansSerif", Font.BOLD, 10));

            JTextArea txtProblema = new JTextArea("○ " + inc.getProblema());
            txtProblema.setWrapStyleWord(true); txtProblema.setLineWrap(true);
            txtProblema.setEditable(false); txtProblema.setOpaque(false);
            txtProblema.setFont(new Font("SansSerif", Font.PLAIN, 12));

            card.add(lblTag, BorderLayout.NORTH); card.add(txtProblema, BorderLayout.CENTER);

            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    incidenciaSeleccionada = inc;
                    if (tarjetaVisualSeleccionada != null) tarjetaVisualSeleccionada.setBorder(obtenerBordeNormal());
                    tarjetaVisualSeleccionada = card;
                    tarjetaVisualSeleccionada.setBorder(obtenerBordeResaltado());
                    consola.setText("> TARJETA SELECCIONADA:\n------------------------------------------\n" + inc.getDetalleCompleto() + "\n------------------------------------------\n");
                }
            });

            if (inc == incidenciaSeleccionada) {
                tarjetaVisualSeleccionada = card; card.setBorder(obtenerBordeResaltado());
            }
            return card;
        }

        private void ejecutarAccionBoton(String accion) {
            if (incidenciaSeleccionada == null) {
                consola.append("\n[ERROR] Selecciona una tarjeta del tablero primero.\n");
                return;
            }
            if (incidenciaSeleccionada.getEstado() == Estado.COMPLETADO) {
                consola.append("\n[INFO] Esta incidencia ya ha sido resuelta.\n");
                return;
            }
            if (incidenciaSeleccionada.isResolucionEnMarcha()) {
                consola.append("\n[SISTEMA] Ya hay un protocolo en marcha para esta tarjeta.\n");
                return;
            }

            if (accion.equals("Tomar Caso (Mover a En Curso)")) {
                incidenciaSeleccionada.setEstado(Estado.EN_CURSO);
                consola.append("> Tarjeta movida a la columna 'En Curso' manualmente.\n");
                controlador.actualizarVista();
                return;
            }

            List<String> soluciones = controlador.matrizSoluciones.get(incidenciaSeleccionada.getProblema());
            if (soluciones != null && soluciones.contains(accion)) {
                iniciarSimulacionTiempoReal(incidenciaSeleccionada, accion);
            } else {
                consola.append("\n[FALLO] La acción '" + accion + "' no aplica para solucionar este problema.\n");
            }
        }

        // --- SISTEMA DE TIEMPO REAL CON SWINGWORKER ---
        private void iniciarSimulacionTiempoReal(Incidencia inc, String accion) {
            inc.setEstado(Estado.EN_CURSO);
            inc.setResolucionEnMarcha(true); // Bloquea múltiples clics en la misma tarjeta
            controlador.actualizarVista();
            consola.append("\n> Acción válida: [" + accion + "]. Iniciando protocolo en tiempo real...\n");

            int tiempoTotalSegundos;
            String[] mensajesSimulados;
            String problema = inc.getProblema();

            // Lógica de Tiempos basada en la complejidad
            if (problema.contains("Focos") || problema.contains("Basura") || problema.contains("Mascotas") || problema.contains("Ruidos")) {
                tiempoTotalSegundos = 40; // Casos Leves
                mensajesSimulados = new String[]{
                    "Contactando al personal asignado...", 
                    "Personal dirigiéndose a la ubicación (Dpto " + inc.getDepartamento() + ")...", 
                    "Ejecutando labores de atención rápida...", 
                    "Verificando la limpieza/resolución en el área..."
                };
            } else if (problema.contains("Ascensor") || problema.contains("Robo") || problema.contains("Incendio") || problema.contains("gas") || problema.contains("médica") || problema.contains("Corte") || problema.contains("sismo")) {
                tiempoTotalSegundos = 80; // Casos Críticos / Emergencias
                mensajesSimulados = new String[]{
                    "¡ALERTA! Iniciando protocolo de alta prioridad...", 
                    "Contactando a entidades externas de emergencia...", 
                    "Unidades confirmadas y en camino al condominio...", 
                    "Unidades operando en el área afectada...", 
                    "Controlando la situación crítica y asegurando perímetro...", 
                    "Emergencia controlada. Elaborando reporte final de daños..."
                };
            } else {
                tiempoTotalSegundos = 60; // Casos Medios
                mensajesSimulados = new String[]{
                    "Evaluando la situación reportada...", 
                    "Derivando al especialista / equipo de mantenimiento...", 
                    "Equipo en el lugar analizando el desperfecto...", 
                    "Ejecutando reparaciones técnicas necesarias...", 
                    "Realizando pruebas de funcionamiento..."
                };
            }

            // Calculamos cada cuántos milisegundos se publicará un mensaje
            int intervaloMs = (tiempoTotalSegundos * 1000) / (mensajesSimulados.length + 1);

            SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() throws Exception {
                    for (String msg : mensajesSimulados) {
                        Thread.sleep(intervaloMs);
                        publish("> [TIEMPO REAL] " + msg);
                    }
                    Thread.sleep(intervaloMs); // Última espera antes de completar
                    return null;
                }

                @Override
                protected void process(List<String> chunks) {
                    for (String texto : chunks) {
                        consola.append(texto + "\n");
                        consola.setCaretPosition(consola.getDocument().getLength()); // Auto-scroll hacia abajo
                    }
                }

                @Override
                protected void done() {
                    inc.setEstado(Estado.COMPLETADO);
                    inc.setResolucionEnMarcha(false);
                    controlador.actualizarVista();
                    consola.append("\n[ÉXITO] Acción '" + accion + "' finalizada. Tarjeta desplazada a 'Completado'.\n");
                    consola.setCaretPosition(consola.getDocument().getLength());
                }
            };
            worker.execute();
        }
        @Override
        public void onIncidenciaActualizada() {
            cargarTarjetas();
        }
    }
}