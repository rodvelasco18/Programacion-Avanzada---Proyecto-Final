package sistemacondominio;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SistemaCondominio {

    // --- PALETA DE COLORES MINIMALISTA CON ACCENTOS COLORIDOS ---
    public static final Color COLOR_BG = new Color(248, 250, 252);           // #F8FAFC
    public static final Color COLOR_SURFACE = new Color(255, 255, 255);       // #FFFFFF
    public static final Color COLOR_BORDER = new Color(226, 232, 240);        // #E2E8F0
    public static final Color COLOR_BORDER_ACTIVE = new Color(194, 203, 207);   // #4F46E5
    
    public static final Color COLOR_TEXT_PRIMARY = new Color(15, 23, 42);     // #0F172A
    public static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);    // #64748B
    
    // Botón Primario
    public static final Color COLOR_BTN_PRIMARY = new Color(194, 203, 207);      // #4F46E5 (Indigo Vivo)
    public static final Color COLOR_BTN_HOVER = new Color(67, 56, 202);

    // Estados de Kanban (Píldoras de Color)
    public static final Color BG_PENDIENTE = new Color(255, 251, 235);        // Ámbar suave
    public static final Color FG_PENDIENTE = new Color(180, 83, 9);
    
    public static final Color BG_EN_CURSO = new Color(239, 246, 255);         // Azul cielo suave
    public static final Color FG_EN_CURSO = new Color(29, 78, 216);
    
    public static final Color BG_COMPLETADO = new Color(236, 253, 245);       // Verde menta
    public static final Color FG_COMPLETADO = new Color(4, 120, 87);

    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_MEDIUM = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 14);

    // --- COMPONENTES UI CON ESTILO ---

    /** Botón Moderno con soporte de colores personalizados */
    public static class BotonMinimalista extends JButton {
        private Color bgNormal = COLOR_SURFACE;
        private Color bgHover = new Color(238, 242, 255);
        private Color colorTexto = COLOR_TEXT_PRIMARY;
        private Color colorBorde = COLOR_BORDER;
        private boolean hover = false;
        private int radio = 8;

        public BotonMinimalista(String texto) {
            super(texto);
            init();
        }

        public BotonMinimalista(String texto, Color bgNormal, Color bgHover, Color colorTexto) {
            super(texto);
            this.bgNormal = bgNormal;
            this.bgHover = bgHover;
            this.colorTexto = colorTexto;
            this.colorBorde = bgNormal;
            init();
        }

        private void init() {
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(colorTexto);
            setFont(FONT_REGULAR);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(6, 12, 6, 12));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        public void setRadio(int radio) { this.radio = radio; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(hover ? bgHover : bgNormal);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radio, radio));

            if (colorBorde != null) {
                g2.setColor(colorBorde);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radio, radio));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Contenedor Redondeado Limpio */
    public static class PanelMinimalista extends JPanel {
        private int radio = 10;
        private Color fondo = COLOR_SURFACE;
        private Color borde = COLOR_BORDER;

        public PanelMinimalista(int radio, Color fondo, Color borde) {
            this.radio = radio;
            this.fondo = fondo;
            this.borde = borde;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (fondo != null) {
                g2.setColor(fondo);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radio, radio));
            }

            if (borde != null) {
                g2.setColor(borde);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radio, radio));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

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
        protected boolean resolucionEnMarcha;

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

    // --- 3. COMPONENTE VISUAL DE FONDO NEUTRO ---
    static class PanelFondo extends JPanel {
        public PanelFondo(String rutaImagen) {
            setBackground(COLOR_BG);
        }
    }

    // --- 4. VISTAS (INTERFAZ GRÁFICA) ---
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            ControladorSistema controlador = new ControladorSistema();
            new VentanaLoginAdmin(controlador).setVisible(true);
            new AppLoginResidente(controlador).setVisible(true);
        });
    }

    static class VentanaLoginAdmin extends JFrame {
        public VentanaLoginAdmin(ControladorSistema ctrl) {
            setTitle("Login");
            setSize(360, 290);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            getContentPane().setBackground(COLOR_BG);
            setLayout(new BorderLayout());

            PanelMinimalista cardPanel = new PanelMinimalista(12, COLOR_SURFACE, COLOR_BORDER);
            cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
            cardPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

            JLabel lblTitulo = new JLabel("Administración");
            lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblTitulo.setFont(FONT_TITLE);
            lblTitulo.setForeground(COLOR_TEXT_PRIMARY);

            JTextField txtUser = new JTextField();
            JPasswordField txtPass = new JPasswordField();
            txtUser.setFont(FONT_REGULAR);
            txtPass.setFont(FONT_REGULAR);

            JPanel form = new JPanel(new GridLayout(2, 2, 8, 12));
            form.setOpaque(false);
            form.setBorder(new EmptyBorder(16, 0, 16, 0));
            
            JLabel lblUser = new JLabel("Usuario");
            lblUser.setFont(FONT_REGULAR);
            lblUser.setForeground(COLOR_TEXT_MUTED);
            
            JLabel lblPass = new JLabel("Contraseña");
            lblPass.setFont(FONT_REGULAR);
            lblPass.setForeground(COLOR_TEXT_MUTED);

            form.add(lblUser); form.add(txtUser);
            form.add(lblPass); form.add(txtPass);

            BotonMinimalista btnIngresar = new BotonMinimalista("Ingresar", COLOR_BTN_PRIMARY, COLOR_BTN_HOVER, Color.WHITE);
            BotonMinimalista btnCrear = new BotonMinimalista("Crear cuenta");
            BotonMinimalista btnRecuperar = new BotonMinimalista("Recuperar");

            btnIngresar.setAlignmentX(Component.CENTER_ALIGNMENT);

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
                Object[] campos = {"Nuevo Usuario:", nUser, "Contraseña:", nPass, "DNI (8 dígitos):", nDni};
                if (JOptionPane.showConfirmDialog(this, campos, "Crear Administrador", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                    try {
                        ctrl.crearUsuarioAdmin(nUser.getText(), nPass.getText(), nDni.getText());
                        JOptionPane.showMessageDialog(this, "Usuario creado exitosamente.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            btnRecuperar.addActionListener(e -> {
                String dniStr = JOptionPane.showInputDialog(this, "Ingrese DNI:");
                if (dniStr != null) {
                    UsuarioAdmin rec = ctrl.recuperarCredenciales(dniStr);
                    if (rec != null) JOptionPane.showMessageDialog(this, "Usuario: " + rec.getUsuario() + "\nContraseña: " + rec.getPassword(), "Recuperado", JOptionPane.INFORMATION_MESSAGE);
                    else JOptionPane.showMessageDialog(this, "No se encontró el DNI.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            JPanel pnlSubOpciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
            pnlSubOpciones.setOpaque(false);
            pnlSubOpciones.add(btnCrear);
            pnlSubOpciones.add(btnRecuperar);

            cardPanel.add(lblTitulo);
            cardPanel.add(form);
            cardPanel.add(btnIngresar);
            cardPanel.add(Box.createVerticalStrut(12));
            cardPanel.add(pnlSubOpciones);

            JPanel root = new JPanel(new BorderLayout());
            root.setOpaque(false);
            root.setBorder(new EmptyBorder(16, 16, 16, 16));
            root.add(cardPanel, BorderLayout.CENTER);

            add(root);
        }
    }

    static class AppLoginResidente extends JFrame {
        public AppLoginResidente(ControladorSistema ctrl) {
            setTitle("App Residente");
            setSize(360, 600);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocation(50, 50); 
            getContentPane().setBackground(COLOR_BG);

            PanelMinimalista container = new PanelMinimalista(12, COLOR_SURFACE, COLOR_BORDER);
            container.setLayout(new GridBagLayout());
            container.setBorder(new EmptyBorder(24, 24, 24, 24));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 6, 6, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0; gbc.gridy = 0;

            JLabel lblTitulo = new JLabel("Acceso Residente", SwingConstants.CENTER);
            lblTitulo.setFont(FONT_TITLE);
            lblTitulo.setForeground(COLOR_TEXT_PRIMARY);
            container.add(lblTitulo, gbc);

            gbc.gridy++; 
            JLabel lblSub = new JLabel("Nombre y Apellido", SwingConstants.LEFT);
            lblSub.setFont(FONT_REGULAR);
            lblSub.setForeground(COLOR_TEXT_MUTED);
            container.add(lblSub, gbc);

            gbc.gridy++; 
            JTextField txtNombre = new JTextField(20);
            txtNombre.setFont(FONT_REGULAR);
            container.add(txtNombre, gbc);

            gbc.gridy++; 
            BotonMinimalista btnInquilino = new BotonMinimalista("Soy Inquilino", COLOR_BTN_PRIMARY, COLOR_BTN_HOVER, Color.WHITE); 
            container.add(btnInquilino, gbc);

            gbc.gridy++; 
            BotonMinimalista btnPropietario = new BotonMinimalista("Soy Propietario"); 
            container.add(btnPropietario, gbc);

            btnInquilino.addActionListener(e -> ingresar(ctrl, txtNombre.getText(), "Inquilino"));
            btnPropietario.addActionListener(e -> ingresar(ctrl, txtNombre.getText(), "Propietario"));

            JPanel root = new JPanel(new BorderLayout());
            root.setBorder(new EmptyBorder(16, 16, 16, 16));
            root.setOpaque(false);
            root.add(container, BorderLayout.CENTER);
            add(root);
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
            setTitle("Reporte");
            setSize(360, 600); 
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocation(50, 50);
            getContentPane().setBackground(COLOR_BG);

            JPanel panelPrincipal = new JPanel();
            panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
            panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            panelPrincipal.setBackground(COLOR_SURFACE);

            JLabel lblUser = new JLabel(nombre + " • " + tipo);
            lblUser.setFont(FONT_MEDIUM);
            lblUser.setForeground(COLOR_BTN_PRIMARY);

            JPanel panelDpto = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
            panelDpto.setOpaque(false);
            JLabel lblPiso = new JLabel("Piso:"); lblPiso.setFont(FONT_REGULAR);
            panelDpto.add(lblPiso);
            JComboBox<String> cbxPiso = new JComboBox<>(new String[]{"1","2","3","4","5","6","7","8","9"});
            panelDpto.add(cbxPiso);
            JLabel lblPuerta = new JLabel(" Dpto:"); lblPuerta.setFont(FONT_REGULAR);
            panelDpto.add(lblPuerta);
            JComboBox<String> cbxPuerta = new JComboBox<>(new String[]{"01","02"});
            panelDpto.add(cbxPuerta);

            JComboBox<String> cbxIncidencia = new JComboBox<>(incidenciasComunes);
            JTextField txtDesc = new JTextField();
            txtDesc.setFont(FONT_REGULAR);

            BotonMinimalista btnEnviar = new BotonMinimalista("Enviar Reporte", COLOR_BTN_PRIMARY, COLOR_BTN_HOVER, Color.WHITE);
            BotonMinimalista btnLimpiar = new BotonMinimalista("Limpiar");
            BotonMinimalista btnVolver = new BotonMinimalista("Cerrar Sesión");

            btnEnviar.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnLimpiar.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnVolver.setAlignmentX(Component.CENTER_ALIGNMENT);

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

            panelPrincipal.add(lblUser);
            panelPrincipal.add(Box.createVerticalStrut(12));
            panelPrincipal.add(panelDpto);
            panelPrincipal.add(Box.createVerticalStrut(12));
            
            JLabel lblTipo = new JLabel("Tipo de Incidencia"); lblTipo.setFont(FONT_REGULAR);
            panelPrincipal.add(lblTipo);
            panelPrincipal.add(cbxIncidencia);
            panelPrincipal.add(Box.createVerticalStrut(12));
            
            JLabel lblDesc = new JLabel("Descripción"); lblDesc.setFont(FONT_REGULAR);
            panelPrincipal.add(lblDesc);
            panelPrincipal.add(txtDesc);
            panelPrincipal.add(Box.createVerticalStrut(24));
            
            panelPrincipal.add(btnEnviar);
            panelPrincipal.add(Box.createVerticalStrut(6));
            panelPrincipal.add(btnLimpiar);
            panelPrincipal.add(Box.createVerticalStrut(6));
            panelPrincipal.add(btnVolver);

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

            setTitle("Dashboard - Gestión de Condominio");
            setSize(1150, 750);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            PanelFondo fondoTablero = new PanelFondo("ui.jpeg");
            fondoTablero.setLayout(new BorderLayout());

            JPanel panelTitulos = new JPanel(new GridLayout(1, 3, 16, 0));
            panelTitulos.setOpaque(false);
            panelTitulos.setBorder(new EmptyBorder(16, 16, 8, 16));
            panelTitulos.add(crearHeaderColumna("Pendientes", BG_PENDIENTE, FG_PENDIENTE));
            panelTitulos.add(crearHeaderColumna("En curso", BG_EN_CURSO, FG_EN_CURSO));
            panelTitulos.add(crearHeaderColumna("Completados", BG_COMPLETADO, FG_COMPLETADO));
            fondoTablero.add(panelTitulos, BorderLayout.NORTH);

            JPanel panelColumnas = new JPanel(new GridLayout(1, 3, 16, 0));
            panelColumnas.setOpaque(false);
            panelColumnas.setBorder(new EmptyBorder(0, 16, 12, 16));

            panelPendiente = crearColumnaListado();
            panelEnCurso = crearColumnaListado();
            panelCompletado = crearColumnaListado();

            panelColumnas.add(crearScrollTransparente(panelPendiente));
            panelColumnas.add(crearScrollTransparente(panelEnCurso));
            panelColumnas.add(crearScrollTransparente(panelCompletado));
            fondoTablero.add(panelColumnas, BorderLayout.CENTER);

            JPanel panelInferior = new JPanel(new BorderLayout());
            panelInferior.setBackground(COLOR_BG);
            
            consola = new JTextArea(6, 0);
            consola.setEditable(false);
            consola.setFont(new Font("Consolas", Font.PLAIN, 11));
            consola.setBackground(new Color(15, 23, 42));
            consola.setForeground(new Color(52, 211, 153)); // Verde menta para logs
            consola.setCaretColor(Color.WHITE);
            consola.setText("> Sistema Kanban Iniciado.\n> Seleccione una tarjeta para gestionar...\n");
            
            JPanel panelBotonesAcciones = new JPanel(new GridLayout(0, 4, 6, 6));
            panelBotonesAcciones.setOpaque(false);
            panelBotonesAcciones.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            
            for (String accion : accionesPosibles) {
                BotonMinimalista btn = new BotonMinimalista(accion);
                btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                btn.setRadio(6);
                btn.addActionListener(e -> ejecutarAccionBoton(accion));
                panelBotonesAcciones.add(btn);
            }

            JPanel panelControlesExtra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
            panelControlesExtra.setOpaque(false);

            BotonMinimalista btnLimpiar = new BotonMinimalista("Eliminar", COLOR_SURFACE, new Color(254, 226, 226), new Color(220, 38, 38));
            BotonMinimalista btnCerrar = new BotonMinimalista("Cerrar Sesión");
            
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
            panelSurBotones.setOpaque(false);
            panelSurBotones.add(panelBotonesAcciones, BorderLayout.CENTER);
            panelSurBotones.add(panelControlesExtra, BorderLayout.SOUTH);

            JScrollPane scrollConsola = new JScrollPane(consola);
            scrollConsola.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));

            panelInferior.add(scrollConsola, BorderLayout.CENTER);
            panelInferior.add(panelSurBotones, BorderLayout.SOUTH);

            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, fondoTablero, panelInferior);
            splitPane.setDividerLocation(450); 
            splitPane.setBorder(null);
            add(splitPane);
            cargarTarjetas();
        }

        private JPanel crearHeaderColumna(String texto, Color bg, Color fg) {
            PanelMinimalista panel = new PanelMinimalista(8, bg, null);
            panel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));
            JLabel lbl = new JLabel(texto);
            lbl.setFont(FONT_MEDIUM);
            lbl.setForeground(fg); 
            panel.add(lbl);
            return panel;
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
            return BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 8, 0),
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true)
            );
        }

        private Border obtenerBordeResaltado() {
            return BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 8, 0),
                BorderFactory.createLineBorder(COLOR_BORDER_ACTIVE, 2, true)
            );
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
            PanelMinimalista card = new PanelMinimalista(8, COLOR_SURFACE, null);
            card.setLayout(new BorderLayout(6, 6));
            card.setMaximumSize(new Dimension(350, 90));
            card.setBorder(BorderFactory.createCompoundBorder(obtenerBordeNormal(), new EmptyBorder(10, 12, 10, 12)));
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Badge de Departamento con acento en Indigo
            JLabel lblTag = new JLabel("Dpto " + inc.getDepartamento());
            lblTag.setFont(new Font("Segoe UI", Font.BOLD, 10));
            lblTag.setForeground(new Color(67, 56, 202));
            lblTag.setBackground(new Color(238, 242, 255));
            lblTag.setOpaque(true);
            lblTag.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

            JTextArea txtProblema = new JTextArea(inc.getProblema());
            txtProblema.setWrapStyleWord(true); txtProblema.setLineWrap(true);
            txtProblema.setEditable(false); txtProblema.setOpaque(false);
            txtProblema.setFont(FONT_REGULAR);
            txtProblema.setForeground(COLOR_TEXT_PRIMARY);

            JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            pnlTop.setOpaque(false);
            pnlTop.add(lblTag);

            card.add(pnlTop, BorderLayout.NORTH); 
            card.add(txtProblema, BorderLayout.CENTER);

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

        private void iniciarSimulacionTiempoReal(Incidencia inc, String accion) {
            inc.setEstado(Estado.EN_CURSO);
            inc.setResolucionEnMarcha(true);
            controlador.actualizarVista();
            consola.append("\n> Acción válida: [" + accion + "]. Iniciando protocolo en tiempo real...\n");

            int tiempoTotalSegundos;
            String[] mensajesSimulados;
            String problema = inc.getProblema();

            if (problema.contains("Focos") || problema.contains("Basura") || problema.contains("Mascotas") || problema.contains("Ruidos")) {
                tiempoTotalSegundos = 40;
                mensajesSimulados = new String[]{
                    "Contactando al personal asignado...", 
                    "Personal dirigiéndose a la ubicación (Dpto " + inc.getDepartamento() + ")...", 
                    "Ejecutando labores de atención rápida...", 
                    "Verificando la limpieza/resolución en el área..."
                };
            } else if (problema.contains("Ascensor") || problema.contains("Robo") || problema.contains("Incendio") || problema.contains("gas") || problema.contains("médica") || problema.contains("Corte") || problema.contains("sismo")) {
                tiempoTotalSegundos = 80;
                mensajesSimulados = new String[]{
                    "¡ALERTA! Iniciando protocolo de alta prioridad...", 
                    "Contactando a entidades externas de emergencia...", 
                    "Unidades confirmadas y en camino al condominio...", 
                    "Unidades operando en el área afectada...", 
                    "Controlando la situación crítica y asegurando perímetro...", 
                    "Emergencia controlada. Elaborando reporte final de daños..."
                };
            } else {
                tiempoTotalSegundos = 60;
                mensajesSimulados = new String[]{
                    "Evaluando la situación reportada...", 
                    "Derivando al especialista / equipo de mantenimiento...", 
                    "Equipo en el lugar analizando el desperfecto...", 
                    "Ejecutando reparaciones técnicas necesarias...", 
                    "Realizando pruebas de funcionamiento..."
                };
            }

            int intervaloMs = (tiempoTotalSegundos * 1000) / (mensajesSimulados.length + 1);

            SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() throws Exception {
                    for (String msg : mensajesSimulados) {
                        Thread.sleep(intervaloMs);
                        publish("> [TIEMPO REAL] " + msg);
                    }
                    Thread.sleep(intervaloMs);
                    return null;
                }

                @Override
                protected void process(List<String> chunks) {
                    for (String texto : chunks) {
                        consola.append(texto + "\n");
                        consola.setCaretPosition(consola.getDocument().getLength());
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