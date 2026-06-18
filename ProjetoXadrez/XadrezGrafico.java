import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class XadrezGrafico extends JFrame {

    private CardLayout painelPreco = new CardLayout();
    private JPanel painelPrincipal = new JPanel(painelPreco);

    private JButton[][] casas = new JButton[8][8];
    private String[][] tabuleiroLogico = new String[8][8];
    private boolean turnoBrancas = true;
    private boolean contraIA = false; 

    private int linhaSelecionada = -1;
    private int colunaSelecionada = -1;

    private final String PEAO_B = "p_b", TORRE_B = "t_b", CAVALO_B = "c_b", BISPO_B = "b_b", DAMA_B = "d_b", REI_B = "r_b";
    private final String PEAO_P = "p_p", TORRE_P = "t_p", CAVALO_P = "c_p", BISPO_P = "b_p", DAMA_P = "d_p", REI_P = "r_p";

    public XadrezGrafico() {
        setTitle("Xadrez");
        setSize(650, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel telaMenu = criarPainelMenu();
        JPanel telaJogo = criarPainelJogo();

        painelPrincipal.add(telaMenu, "MENU");
        painelPrincipal.add(telaJogo, "JOGO");

        add(painelPrincipal);
        painelPreco.show(painelPrincipal, "MENU");
    }

    private JPanel criarPainelMenu() {
        JPanel menu = new JPanel(new GridBagLayout());
        menu.setBackground(new Color(30, 31, 34)); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0); 

        JLabel titulo = new JLabel("XADREZ", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 42));
        titulo.setForeground(new Color(245, 245, 245));
        titulo.setBorder(new EmptyBorder(0, 0, 5, 0));
        gbc.gridy = 0;
        menu.add(titulo, gbc);

        JLabel subtitulo = new JLabel("Edição Minimalista Neon", SwingConstants.CENTER);
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitulo.setForeground(new Color(59, 130, 246)); 
        subtitulo.setBorder(new EmptyBorder(0, 0, 30, 0));
        gbc.gridy = 1;
        menu.add(subtitulo, gbc);

        JButton botao1vs1 = criarBotaoMenu("JOGAR 1 VS 1");
        botao1vs1.addActionListener(e -> {
            contraIA = false;
            reiniciarJogo();
            painelPreco.show(painelPrincipal, "JOGO");
            setTitle("Xadrez - Turno: Brancas");
        });
        gbc.gridy = 2;
        menu.add(botao1vs1, gbc);

        JButton botaoIA = criarBotaoMenu("CONTRA IA");
        botaoIA.addActionListener(e -> {
            contraIA = true;
            reiniciarJogo();
            painelPreco.show(painelPrincipal, "JOGO");
            setTitle("Xadrez - Modo: Contra IA (Sua Vez)");
        });
        gbc.gridy = 3;
        menu.add(botaoIA, gbc);

        return menu;
    }

    private JButton criarBotaoMenu(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("SansSerif", Font.BOLD, 15));
        botao.setBackground(new Color(59, 130, 246)); 
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(12, 35, 12, 35));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(new Color(37, 99, 235));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(new Color(59, 130, 246));
            }
        });
        return botao;
    }

    private JPanel criarPainelJogo() {
        JPanel painelTabuleiro = new JPanel(new GridLayout(8, 8));
        inicializarLogica();

        ButtonListener listener = new ButtonListener();

        for (int l = 0; l < 8; l++) {
            for (int c = 0; c < 8; c++) {
                casas[l][c] = new JButton();
                casas[l][c].setFocusPainted(false);
                
                atualizarSpriteNaTela(l, c);
                
                casas[l][c].putClientProperty("linha", l);
                casas[l][c].putClientProperty("coluna", c);
                casas[l][c].addActionListener(listener);

                limparCorDaCasa(l, c);
                painelTabuleiro.add(casas[l][c]);
            }
        }
        return painelTabuleiro;
    }

    private void inicializarLogica() {
        tabuleiroLogico[0] = new String[]{TORRE_P, CAVALO_P, BISPO_P, DAMA_P, REI_P, BISPO_P, CAVALO_P, TORRE_P};
        for (int c = 0; c < 8; c++) tabuleiroLogico[1][c] = PEAO_P;

        for (int l = 2; l <= 5; l++) {
            for (int c = 0; c < 8; c++) tabuleiroLogico[l][c] = "";
        }

        for (int c = 0; c < 8; c++) tabuleiroLogico[6][c] = PEAO_B;
        tabuleiroLogico[7] = new String[]{TORRE_B, CAVALO_B, BISPO_B, DAMA_B, REI_B, BISPO_B, CAVALO_B, TORRE_B};
    }

    private void atualizarSpriteNaTela(int l, int c) {
        String nomePeca = tabuleiroLogico[l][c];
        if (!nomePeca.isEmpty()) {
            boolean ehBranca = ehPecaBranca(nomePeca);
            String tipo = nomePeca.substring(0, 1);
            casas[l][c].setIcon(new IconePecaMinimalista(tipo, ehBranca, 50));
        } else {
            casas[l][c].setIcon(null);
        }
    }

    private void limparCorDaCasa(int l, int c) {
        if ((l + c) % 2 == 0) {
            casas[l][c].setBackground(new Color(43, 45, 49)); 
        } else {
            casas[l][c].setBackground(new Color(30, 31, 34)); 
        }
    }

    private void resetarCoresDoTabuleiro() {
        for (int l = 0; l < 8; l++) {
            for (int c = 0; c < 8; c++) {
                limparCorDaCasa(l, c);
            }
        }
        // Sempre reaplica o destaque visual se houver algum rei em perigo
        destacarReiEmXeque();
    }

    private void destacarMovimentosPossiveis(int origL, int origC) {
        for (int l = 0; l < 8; l++) {
            for (int c = 0; c < 8; c++) {
                if (movimentoValido(origL, origC, l, c)) {
                    String pecaDestino = tabuleiroLogico[l][c];
                    
                    if (pecaDestino.isEmpty() || ehPecaBranca(tabuleiroLogico[origL][origC]) != ehPecaBranca(pecaDestino)) {
                        if (pecaDestino.isEmpty()) {
                            casas[l][c].setBackground(new Color(59, 130, 246, 120)); 
                        } else {
                            casas[l][c].setBackground(new Color(239, 68, 68, 140)); 
                        }
                    }
                }
            }
        }
    }

    private boolean ehPecaBranca(String peca) {
        return peca.contains("_b");
    }

    // Seu método adaptado: Pinta a casa do Rei de Vermelho se estiver em xeque
    private void destacarReiEmXeque() {
        if (reiEmXeque(true)) {
            for (int l = 0; l < 8; l++) {
                for (int c = 0; c < 8; c++) {
                    if (tabuleiroLogico[l][c].equals(REI_B)) {
                        casas[l][c].setBackground(new Color(239, 68, 68)); // Vermelho elegante
                    }
                }
            }
        }

        if (reiEmXeque(false)) {
            for (int l = 0; l < 8; l++) {
                for (int c = 0; c < 8; c++) {
                    if (tabuleiroLogico[l][c].equals(REI_P)) {
                        casas[l][c].setBackground(new Color(239, 68, 68));
                    }
                }
            }
        }
    }

    // RESOLVIDO: Lógica real do Xeque implementada com sucesso!
    private boolean reiEmXeque(boolean verificarReiBranco) {
        int reiL = -1, reiC = -1;
        String alvoRei = verificarReiBranco ? REI_B : REI_P;

        // 1. Encontra onde o Rei está no mapa lógico
        for (int l = 0; l < 8; l++) {
            for (int c = 0; c < 8; c++) {
                if (tabuleiroLogico[l][c].equals(alvoRei)) {
                    reiL = l;
                    reiC = c;
                    break;
                }
            }
        }

        if (reiL == -1) return false; // Caso o rei tenha sido capturado

        // 2. Verifica se alguma peça inimiga consegue alcançar a casa do Rei
        for (int l = 0; l < 8; l++) {
            for (int c = 0; c < 8; c++) {
                String peca = tabuleiroLogico[l][c];
                if (!peca.isEmpty() && ehPecaBranca(peca) != verificarReiBranco) {
                    if (movimentoValido(l, c, reiL, reiC)) {
                        return true; 
                    }
                }
            }
        }
        return false;
    }

    private boolean movimentoValido(int origemL, int origemC, int destinoL, int destinoC) {
        String peca = tabuleiroLogico[origemL][origemC];
        if (peca.isEmpty()) return false;
        if (origemL == destinoL && origemC == destinoC) return false;

        int diffL = Math.abs(destinoL - origemL);
        int diffC = Math.abs(destinoC - origemC);

        if (peca.equals(PEAO_B)) {
            if (destinoC == origemC && destinoL == origemL - 1 && tabuleiroLogico[destinoL][destinoC].isEmpty()) return true;
            if (origemL == 6 && destinoC == origemC && destinoL == 4 && tabuleiroLogico[5][origemC].isEmpty() && tabuleiroLogico[4][origemC].isEmpty()) return true;
            if (destinoL == origemL - 1 && diffC == 1 && !tabuleiroLogico[destinoL][destinoC].isEmpty() && !ehPecaBranca(tabuleiroLogico[destinoL][destinoC])) return true;
        }

        if (peca.equals(PEAO_P)) {
            if (destinoC == origemC && destinoL == origemL + 1 && tabuleiroLogico[destinoL][destinoC].isEmpty()) return true;
            if (origemL == 1 && destinoC == origemC && destinoL == 3 && tabuleiroLogico[2][origemC].isEmpty() && tabuleiroLogico[3][origemC].isEmpty()) return true;
            if (destinoL == origemL + 1 && diffC == 1 && !tabuleiroLogico[destinoL][destinoC].isEmpty() && ehPecaBranca(tabuleiroLogico[destinoL][destinoC])) return true;
        }

        if (peca.equals(CAVALO_B) || peca.equals(CAVALO_P)) {
            if ((diffL == 2 && diffC == 1) || (diffL == 1 && diffC == 2)) return true;
        }

        if (peca.equals(TORRE_B) || peca.equals(TORRE_P)) {
            if (origemL == destinoL || ServerCheckCaminhoReto(origemL, origemC, destinoL, destinoC)) return noHaPecasNoCaminhoReto(origemL, origemC, destinoL, destinoC);
        }

        if (peca.equals(BISPO_B) || peca.equals(BISPO_P)) {
            if (diffL == diffC) return noHaPecasNoCaminhoDiagonal(origemL, origemC, destinoL, destinoC);
        }

        if (peca.equals(DAMA_B) || peca.equals(DAMA_P)) {
            if (origemL == destinoL || origemC == destinoC) return noHaPecasNoCaminhoReto(origemL, origemC, destinoL, destinoC);
            else if (diffL == diffC) return noHaPecasNoCaminhoDiagonal(origemL, origemC, destinoL, destinoC);
        }

        if (peca.equals(REI_B) || peca.equals(REI_P)) {
            if (diffL <= 1 && diffC <= 1) return true;
        }

        return false;
    }

    private boolean ServerCheckCaminhoReto(int oL, int oC, int dL, int dC) {
        return oL == dL || oC == dC;
    }

    private boolean noHaPecasNoCaminhoReto(int oL, int oC, int dL, int dC) {
        if (!ServerCheckCaminhoReto(oL, oC, dL, dC)) return false;
        int passoL = Integer.compare(dL, oL);
        int passoC = Integer.compare(dC, oC);
        int l = oL + passoL, c = oC + passoC;
        while (l != dL || c != dC) {
            if (!tabuleiroLogico[l][c].isEmpty()) return false;
            l += passoL; c += passoC;
        }
        return true;
    }

    private boolean noHaPecasNoCaminhoDiagonal(int oL, int oC, int dL, int dC) {
        int passoL = (dL > oL) ? 1 : -1;
        int passoC = (dC > oC) ? 1 : -1;
        int l = oL + passoL, c = oC + passoC;
        while (l != dL && c != dC) {
            if (!tabuleiroLogico[l][c].isEmpty()) return false;
            l += passoL; c += passoC;
        }
        return true;
    }

    private void reiniciarJogo() {
        turnoBrancas = true;
        linhaSelecionada = -1;
        colunaSelecionada = -1;
        inicializarLogica();
        for (int l = 0; l < 8; l++) {
            for (int c = 0; c < 8; c++) {
                limparCorDaCasa(l, c);
                atualizarSpriteNaTela(l, c);
            }
        }
        setTitle("Xadrez");
        painelPreco.show(painelPrincipal, "MENU");
    }

    private void ejecutarJogadaDaIA() {
        class MovimentoIA {
            int oL, oC, dL, dC;
            MovimentoIA(int oL, int oC, int dL, int dC) {
                this.oL = oL; this.oC = oC; this.dL = dL; this.dC = dC;
            }
        }

        List<MovimentoIA> movimentosNormais = new ArrayList<>();
        List<MovimentoIA> capturasPossiveis = new ArrayList<>();

        for (int ol = 0; ol < 8; ol++) {
            for (int oc = 0; oc < 8; oc++) {
                String peca = tabuleiroLogico[ol][oc];
                if (!peca.isEmpty() && !ehPecaBranca(peca)) {
                    for (int dl = 0; dl < 8; dl++) {
                        for (int dc = 0; dc < 8; dc++) {
                            if (movimentoValido(ol, oc, dl, dc)) {
                                String alvo = tabuleiroLogico[dl][dc];
                                if (!alvo.isEmpty() && !ehPecaBranca(alvo)) continue;

                                // Simulação para a IA não se colocar em xeque boba
                                String backupOrigem = tabuleiroLogico[ol][oc];
                                String backupDestino = tabuleiroLogico[dl][dc];
                                tabuleiroLogico[dl][dc] = backupOrigem;
                                tabuleiroLogico[ol][oc] = "";
                                boolean ficouEmXeque = reiEmXeque(false);
                                tabuleiroLogico[ol][oc] = backupOrigem;
                                tabuleiroLogico[dl][dc] = backupDestino;

                                if (ficouEmXeque) continue;

                                if (alvo.isEmpty()) {
                                    movimentosNormais.add(new MovimentoIA(ol, oc, dl, dc));
                                } else {
                                    capturasPossiveis.add(new MovimentoIA(ol, oc, dl, dc));
                                }
                            }
                        }
                    }
                }
            }
        }

        MovimentoIA jogadaEscolhida = null;
        if (!capturasPossiveis.isEmpty()) {
            Collections.shuffle(capturasPossiveis);
            jogadaEscolhida = capturasPossiveis.get(0);
        } else if (!movimentosNormais.isEmpty()) {
            Collections.shuffle(movimentosNormais);
            jogadaEscolhida = movimentosNormais.get(0);
        }

        if (jogadaEscolhida != null) {
            String pecaDefensora = tabuleiroLogico[jogadaEscolhida.dL][jogadaEscolhida.dC];
            
            if (pecaDefensora.equals(REI_B)) {
                tabuleiroLogico[jogadaEscolhida.dL][jogadaEscolhida.dC] = tabuleiroLogico[jogadaEscolhida.oL][jogadaEscolhida.oC];
                tabuleiroLogico[jogadaEscolhida.oL][jogadaEscolhida.oC] = "";
                atualizarSpriteNaTela(jogadaEscolhida.dL, jogadaEscolhida.dC);
                atualizarSpriteNaTela(jogadaEscolhida.oL, jogadaEscolhida.oC);
                JOptionPane.showMessageDialog(null, "A IA capturou seu Rei! Você perdeu!", "Fim de Jogo!", JOptionPane.INFORMATION_MESSAGE);
                reiniciarJogo();
                return;
            }

            tabuleiroLogico[jogadaEscolhida.dL][jogadaEscolhida.dC] = tabuleiroLogico[jogadaEscolhida.oL][jogadaEscolhida.oC];
            tabuleiroLogico[jogadaEscolhida.oL][jogadaEscolhida.oC] = "";
            
            atualizarSpriteNaTela(jogadaEscolhida.dL, jogadaEscolhida.dC);
            atualizarSpriteNaTela(jogadaEscolhida.oL, jogadaEscolhida.oC);
        }

        turnoBrancas = true;
        setTitle("Xadrez - Modo: Contra IA (Sua Vez)");
        
        resetarCoresDoTabuleiro();

        // Alerta pop-up caso o jogador humano tenha ficado em Xeque após o turno da IA
        if (reiEmXeque(true)) {
            JOptionPane.showMessageDialog(null, "Atenção: Seu Rei das BRANCAS está em XEQUE!", "Aviso de Perigo", JOptionPane.WARNING_MESSAGE);
        }
        
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    class IconePecaMinimalista implements Icon {
        private String tipo;
        private boolean ehBranca;
        private int tamanho;

        public IconePecaMinimalista(String tipo, boolean ehBranca, int tamanho) {
            this.tipo = tipo;
            this.ehBranca = ehBranca;
            this.tamanho = tamanho;
        }

        @Override
        public void paintIcon(Component com, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color corPreenchimento = ehBranca ? new Color(245, 245, 245) : new Color(25, 25, 25);
            Color corContorno = ehBranca ? new Color(59, 130, 246) : new Color(163, 163, 163);

            g2.setColor(corPreenchimento);
            g2.setStroke(new BasicStroke(2.5f));

            int centroX = x + tamanho / 2;
            int centroY = y + tamanho / 2;

            switch (tipo) {
                case "p":
                    g2.fillOval(centroX - 10, centroY - 14, 20, 20);
                    g2.setColor(corContorno);
                    g2.drawOval(centroX - 10, centroY - 14, 20, 20);
                    g2.setColor(corPreenchimento);
                    g2.fillRect(centroX - 14, centroY + 10, 28, 5);
                    g2.setColor(corContorno);
                    g2.drawRect(centroX - 14, centroY + 10, 28, 5);
                    break;
                case "t":
                    int[] tx = {centroX-15, centroX+15, centroX+15, centroX+10, centroX+10, centroX+5, centroX+5, centroX-5, centroX-5, centroX-10, centroX-10, centroX-15};
                    int[] ty = {centroY+15, centroY+15, centroY-10, centroY-10, centroY-15, centroY-15, centroY-10, centroY-10, centroY-15, centroY-15, centroY-10, centroY-10};
                    g2.fillPolygon(tx, ty, 12);
                    g2.setColor(corContorno);
                    g2.drawPolygon(tx, ty, 12);
                    break;
                case "c":
                    int[] cx = {centroX-15, centroX+10, centroX+15, centroX+5, centroX-10, centroX-15, centroX-5};
                    int[] cy = {centroY+15, centroY+15, centroY-5, centroY-15, centroY-10, centroY+5, centroY+5};
                    g2.fillPolygon(cx, cy, 7);
                    g2.setColor(corContorno);
                    g2.drawPolygon(cx, cy, 7);
                    break;
                case "b":
                    g2.fillOval(centroX - 11, centroY - 13, 22, 26);
                    g2.setColor(corContorno);
                    g2.drawOval(centroX - 11, centroY - 13, 22, 26);
                    g2.drawLine(centroX, centroY - 5, centroX, centroY + 5);
                    break;
                case "d":
                    int[] dx = {centroX-18, centroX+18, centroX+14, centroX+8, centroX, centroX-8, centroX-14};
                    int[] dy = {centroY+15, centroY+15, centroY-12, centroY-2, centroY-15, centroY-2, centroY-12};
                    g2.fillPolygon(dx, dy, 7);
                    g2.setColor(corContorno);
                    g2.drawPolygon(dx, dy, 7);
                    break;
                case "r":
                    g2.fillRect(centroX - 14, centroY - 5, 28, 20);
                    g2.fillOval(centroX - 14, centroY - 10, 28, 12);
                    g2.setColor(corContorno);
                    g2.drawRect(centroX - 14, centroY - 5, 28, 20);
                    g2.drawOval(centroX - 14, centroY - 10, 28, 12);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawLine(centroX, centroY - 18, centroX, centroY - 10);
                    g2.drawLine(centroX - 4, centroY - 14, centroX + 4, centroY - 14);
                    break;
            }
            g2.dispose();
        }

        @Override public int getIconWidth() { return tamanho; }
        @Override public int getIconHeight() { return tamanho; }
    }

    private class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton botaoClicado = (JButton) e.getSource();
            int l = (int) botaoClicado.getClientProperty("linha");
            int c = (int) botaoClicado.getClientProperty("coluna");
            String pecaClicada = tabuleiroLogico[l][c];

            if (contraIA && !turnoBrancas) return;

            if (linhaSelecionada != -1 && colunaSelecionada != -1) {
                int colunaSelecionada = XadrezGrafico.this.colunaSelecionada;
                
                if (linhaSelecionada == l && colunaSelecionada == c) {
                    resetarCoresDoTabuleiro();
                    linhaSelecionada = -1;
                    XadrezGrafico.this.colunaSelecionada = -1;
                    return;
                }

                if (!pecaClicada.isEmpty() && ehPecaBranca(tabuleiroLogico[linhaSelecionada][colunaSelecionada]) == ehPecaBranca(pecaClicada)) {
                    resetarCoresDoTabuleiro();
                    linhaSelecionada = l;
                    XadrezGrafico.this.colunaSelecionada = c;
                    casas[l][c].setBackground(new Color(234, 179, 8, 180)); 
                    destacarMovimentosPossiveis(l, c); 
                    return;
                }

                if (!movimentoValido(linhaSelecionada, colunaSelecionada, l, c)) {
                    JOptionPane.showMessageDialog(null, "Movimento inválido!");
                    return;
                }

                // BLOQUEIO DE AUTO-XEQUE (Simulação Preventiva)
                String backupOrigem = tabuleiroLogico[linhaSelecionada][colunaSelecionada];
                String backupDestino = tabuleiroLogico[l][c];
                tabuleiroLogico[l][c] = backupOrigem;
                tabuleiroLogico[linhaSelecionada][colunaSelecionada] = "";
                
                boolean movimentoSuicida = reiEmXeque(turnoBrancas);
                
                // Desfaz o teste mental
                tabuleiroLogico[linhaSelecionada][colunaSelecionada] = backupOrigem;
                tabuleiroLogico[l][c] = backupDestino;

                if (movimentoSuicida) {
                    JOptionPane.showMessageDialog(null, "Movimento Proibido! Esse comando colocará seu Rei em XEQUE.", "Erro de Estratégia", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean fimDeJogo = false;
                String mensagemVitoria = "";
                if (pecaClicada.equals(REI_B)) {
                    fimDeJogo = true;
                    mensagemVitoria = "O Rei Branco foi capturado! Fim de jogo.";
                } else if (pecaClicada.equals(REI_P)) {
                    fimDeJogo = true;
                    mensagemVitoria = "O Rei Preto foi capturado! Vitória das BRANCAS!";
                }

                tabuleiroLogico[l][c] = tabuleiroLogico[linhaSelecionada][colunaSelecionada];
                tabuleiroLogico[linhaSelecionada][colunaSelecionada] = "";
                
                atualizarSpriteNaTela(l, c);
                atualizarSpriteNaTela(linhaSelecionada, colunaSelecionada);
                
                resetarCoresDoTabuleiro(); 
                
                linhaSelecionada = -1;
                XadrezGrafico.this.colunaSelecionada = -1;

                if (fimDeJogo) {
                    JOptionPane.showMessageDialog(null, mensagemVitoria, "Fim de Jogo!", JOptionPane.INFORMATION_MESSAGE);
                    reiniciarJogo();
                    return;
                }

                turnoBrancas = !turnoBrancas;

                if (contraIA) {
                    setTitle("Xadrez - Modo: Contra IA (Pensando...)");
                    getContentPane().revalidate();
                    getContentPane().repaint();
                    
                    SwingWorker<Void, Void> worker = new SwingWorker<>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            Thread.sleep(350); 
                            return null;
                        }
                        @Override
                        protected void done() {
                            ejecutarJogadaDaIA();
                        }
                    };
                    worker.execute();
                } else {
                    setTitle("Xadrez - Turno: " + (turnoBrancas ? "Brancas" : "Pretas"));
                    
                    // Alerta no modo 1vs1 se o outro jogador foi colocado em Xeque
                    if (reiEmXeque(turnoBrancas)) {
                        JOptionPane.showMessageDialog(null, "XEQUE! O Rei das " + (turnoBrancas ? "BRANCAS" : "PRETAS") + " está sob ataque!", "Aviso de Xeque", JOptionPane.WARNING_MESSAGE);
                    }
                    resetarCoresDoTabuleiro();
                }

            } else {
                if (pecaClicada.isEmpty()) return;

                if ((turnoBrancas && !ehPecaBranca(pecaClicada)) || (!turnoBrancas && ehPecaBranca(pecaClicada))) {
                    JOptionPane.showMessageDialog(null, "Não é o seu turno!");
                    return;
                }

                linhaSelecionada = l;
                XadrezGrafico.this.colunaSelecionada = c;
                
                resetarCoresDoTabuleiro();
                casas[l][c].setBackground(new Color(234, 179, 8, 180)); 
                destacarMovimentosPossiveis(l, c); 
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new XadrezGrafico().setVisible(true);
        });
    }
}