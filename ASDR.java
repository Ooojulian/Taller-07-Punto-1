import java.util.ArrayList;
import java.util.List;

/**
 * Ejercicio 1 - Presentación 07
 * Analizador Sintáctico Descendente Recursivo (ASDR)
 * 
 * Gramática transformada (sin recursividad por la izquierda):
 *   S  → A B' C
 *   S  → D E
 *   A  → dos B' tres
 *   A  → ε
 *   B' → cuatro C cinco B'
 *   B' → ε
 *   C  → seis A B'
 *   C  → ε
 *   D  → uno A E
 *   D  → B'
 *   E  → tres
 *
 * NOTA: La gramática NO es LL(1) debido a conflicto en S con 'cuatro'.
 *       Para esta implementación, se resuelve el conflicto eligiendo S → A B' C
 *       cuando el token es 'cuatro' (ya que A → ε permite que B' consuma 'cuatro').
 */
public class ASDR {

    // Tipos de tokens
    enum Token {
        UNO, DOS, TRES, CUATRO, CINCO, SEIS, FIN_ARCHIVO
    }

    private Token[] tokens;
    private int pos;
    private Token tokenActual;
    private List<String> traza; // Para registrar las derivaciones

    public ASDR(Token[] tokens) {
        this.tokens = tokens;
        this.pos = 0;
        this.traza = new ArrayList<>();
        this.tokenActual = tokens.length > 0 ? tokens[0] : Token.FIN_ARCHIVO;
    }

    // =============================================
    // Función principal de análisis
    // =============================================
    public boolean analizar() {
        try {
            traza.add("Inicio del análisis");
            traza.add("Cadena: " + cadenaOriginal());
            traza.add("---");
            S();
            if (tokenActual != Token.FIN_ARCHIVO) {
                errorSintaxis(Token.FIN_ARCHIVO);
            }
            traza.add("---");
            traza.add("✓ Análisis exitoso: la cadena pertenece al lenguaje.");
            return true;
        } catch (RuntimeException e) {
            traza.add("---");
            traza.add("✗ Error sintáctico: " + e.getMessage());
            return false;
        }
    }

    // =============================================
    // Funciones para cada no terminal
    // =============================================

    // S → A B' C  |  S → D E
    void S() {
        traza.add("S() → token actual: " + tokenActual);

        // Conjuntos de predicción:
        // PRED(S → A B' C) = { dos, cuatro, seis, $ }
        // PRED(S → D E)    = { uno, cuatro, tres }
        // CONFLICTO en 'cuatro': resolvemos eligiendo S → A B' C
        // (A → ε, luego B' consume 'cuatro')

        if (tokenActual == Token.DOS || tokenActual == Token.SEIS 
            || tokenActual == Token.FIN_ARCHIVO || tokenActual == Token.CUATRO) {
            traza.add("  Aplicando: S → A B' C");
            A();
            Bprima();
            C();
        } else if (tokenActual == Token.UNO || tokenActual == Token.TRES) {
            traza.add("  Aplicando: S → D E");
            D();
            E();
        } else {
            errorSintaxis(Token.DOS, Token.CUATRO, Token.SEIS, Token.UNO, Token.TRES, Token.FIN_ARCHIVO);
        }
    }

    // A → dos B' tres  |  A → ε
    void A() {
        traza.add("A() → token actual: " + tokenActual);

        if (tokenActual == Token.DOS) {
            traza.add("  Aplicando: A → dos B' tres");
            emparejar(Token.DOS);
            Bprima();
            emparejar(Token.TRES);
        } else {
            // A → ε
            // PRED(A → ε) = { cuatro, seis, cinco, tres, $ }
            traza.add("  Aplicando: A → ε");
        }
    }

    // B' → cuatro C cinco B'  |  B' → ε
    void Bprima() {
        traza.add("B'() → token actual: " + tokenActual);

        if (tokenActual == Token.CUATRO) {
            traza.add("  Aplicando: B' → cuatro C cinco B'");
            emparejar(Token.CUATRO);
            C();
            emparejar(Token.CINCO);
            Bprima();
        } else {
            // B' → ε
            // PRED(B' → ε) = { seis, tres, cinco, $ }
            traza.add("  Aplicando: B' → ε");
        }
    }

    // C → seis A B'  |  C → ε
    void C() {
        traza.add("C() → token actual: " + tokenActual);

        if (tokenActual == Token.SEIS) {
            traza.add("  Aplicando: C → seis A B'");
            emparejar(Token.SEIS);
            A();
            Bprima();
        } else {
            // C → ε
            // PRED(C → ε) = { cinco, $ }
            traza.add("  Aplicando: C → ε");
        }
    }

    // D → uno A E  |  D → B'
    void D() {
        traza.add("D() → token actual: " + tokenActual);

        if (tokenActual == Token.UNO) {
            traza.add("  Aplicando: D → uno A E");
            emparejar(Token.UNO);
            A();
            E();
        } else {
            // D → B'
            // PRED(D → B') = { cuatro, tres }
            traza.add("  Aplicando: D → B'");
            Bprima();
        }
    }

    // E → tres
    void E() {
        traza.add("E() → token actual: " + tokenActual);
        traza.add("  Aplicando: E → tres");
        emparejar(Token.TRES);
    }

    // =============================================
    // Función emparejar
    // =============================================
    void emparejar(Token esperado) {
        if (tokenActual == esperado) {
            traza.add("  emparejar(" + esperado + ") ✓");
            pos++;
            tokenActual = (pos < tokens.length) ? tokens[pos] : Token.FIN_ARCHIVO;
        } else {
            errorSintaxis(esperado);
        }
    }

    // =============================================
    // Manejo de errores
    // =============================================
    void errorSintaxis(Token... esperados) {
        StringBuilder sb = new StringBuilder();
        sb.append("Se esperaba ");
        for (int i = 0; i < esperados.length; i++) {
            if (i > 0) sb.append(" o ");
            sb.append(esperados[i]);
        }
        sb.append(" pero se encontró ").append(tokenActual);
        sb.append(" en posición ").append(pos);
        throw new RuntimeException(sb.toString());
    }

    // =============================================
    // Utilidades
    // =============================================
    String cadenaOriginal() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokens.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(tokens[i].name().toLowerCase());
        }
        return sb.toString();
    }

    void imprimirTraza() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    TRAZA DEL ANÁLISIS                       ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        for (String linea : traza) {
            System.out.println("  " + linea);
        }
        System.out.println();
    }

    List<String> getTraza() {
        return traza;
    }

    // =============================================
    // MAIN - Pruebas con múltiples cadenas
    // =============================================
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║       ASDR - Ejercicio 1, Presentación 07                   ║");
        System.out.println("║       Análisis Sintáctico Descendente Recursivo              ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.println("║  Gramática transformada:                                     ║");
        System.out.println("║    S  → A B' C    |  S  → D E                                ║");
        System.out.println("║    A  → dos B' tres  |  A  → ε                               ║");
        System.out.println("║    B' → cuatro C cinco B'  |  B' → ε                         ║");
        System.out.println("║    C  → seis A B'  |  C  → ε                                 ║");
        System.out.println("║    D  → uno A E    |  D  → B'                                ║");
        System.out.println("║    E  → tres                                                 ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();

        // Caso 1: Cadena vacía (S → A B' C, todos ε)
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("CASO 1: Cadena vacía (ε)");
        System.out.println("═══════════════════════════════════════════════════════════════");
        Token[] caso1 = {};
        ASDR asdr1 = new ASDR(caso1);
        asdr1.analizar();
        asdr1.imprimirTraza();

        // Caso 2: "uno tres" (S → D E, D → uno A E con A→ε, E→tres... 
        //          pero eso da "uno tres tres", probemos solo "uno tres")
        // Derivación: S → D E → uno A E E... no, veamos:
        // S → D E → (uno A E) E → uno ε E E → uno tres tres
        // Intentemos: "uno tres"
        // S → D E, D → uno A E: consume "uno", A→ε, E→emparejar(tres)
        // luego S necesita E(): emparejar(tres) pero ya no hay tokens → error
        // Mejor: "uno dos cuatro cinco tres tres"

        // Caso 2: "dos tres" (S → A B' C, A → dos B' tres, B'→ε, emparejar tres, B'→ε, C→ε)
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("CASO 2: \"dos tres\"");
        System.out.println("═══════════════════════════════════════════════════════════════");
        Token[] caso2 = {Token.DOS, Token.TRES};
        ASDR asdr2 = new ASDR(caso2);
        asdr2.analizar();
        asdr2.imprimirTraza();

        // Caso 3: "cuatro cinco" (S→A B' C, A→ε, B'→cuatro C cinco B', C→ε, emparejar cinco, B'→ε, C→ε)
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("CASO 3: \"cuatro cinco\"");
        System.out.println("═══════════════════════════════════════════════════════════════");
        Token[] caso3 = {Token.CUATRO, Token.CINCO};
        ASDR asdr3 = new ASDR(caso3);
        asdr3.analizar();
        asdr3.imprimirTraza();

        // Caso 4: "seis" (S→A B' C, A→ε, B'→ε, C→seis A B', emparejar seis, A→ε, B'→ε)
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("CASO 4: \"seis\"");
        System.out.println("═══════════════════════════════════════════════════════════════");
        Token[] caso4 = {Token.SEIS};
        ASDR asdr4 = new ASDR(caso4);
        asdr4.analizar();
        asdr4.imprimirTraza();

        // Caso 5: "uno tres" → S→D E, D→uno A E (A→ε, E→tres), luego E() necesita tres pero ya se acabó
        // Derivación: S → D E → uno A E  E → uno ε tres E → uno tres tres
        // Así que "uno tres tres" sería válido
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("CASO 5: \"uno tres tres\"");
        System.out.println("═══════════════════════════════════════════════════════════════");
        Token[] caso5 = {Token.UNO, Token.TRES, Token.TRES};
        ASDR asdr5 = new ASDR(caso5);
        asdr5.analizar();
        asdr5.imprimirTraza();

        // Caso 6: "dos cuatro seis tres cinco tres" 
        // S→A B' C, A→dos B' tres: emparejar(dos), B'→cuatro C cinco B': emparejar(cuatro), 
        // C→seis A B': emparejar(seis), A→ε, B'→ε, emparejar(cinco)... veamos paso a paso
        // dos cuatro seis cinco tres
        // A→dos B' tres: dos, B'→cuatro C cinco B': cuatro, C→seis A B': seis, A→ε, B'→ε, cinco, B'→ε, tres
        // Luego B'→ε, C→ε
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("CASO 6: \"dos cuatro seis cinco tres\"");
        System.out.println("═══════════════════════════════════════════════════════════════");
        Token[] caso6 = {Token.DOS, Token.CUATRO, Token.SEIS, Token.CINCO, Token.TRES};
        ASDR asdr6 = new ASDR(caso6);
        asdr6.analizar();
        asdr6.imprimirTraza();

        // Caso 7: Error - "uno dos" (debería fallar)
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("CASO 7 (ERROR): \"uno dos\"");
        System.out.println("═══════════════════════════════════════════════════════════════");
        Token[] caso7 = {Token.UNO, Token.DOS};
        ASDR asdr7 = new ASDR(caso7);
        asdr7.analizar();
        asdr7.imprimirTraza();

        // Caso 8: "tres" → S→D E, D→B' (B'→ε ya que tres ∈ PRED(B'→ε)), E→tres
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("CASO 8: \"tres\"");
        System.out.println("═══════════════════════════════════════════════════════════════");
        Token[] caso8 = {Token.TRES};
        ASDR asdr8 = new ASDR(caso8);
        asdr8.analizar();
        asdr8.imprimirTraza();

        // Caso 9: "uno dos tres tres tres"
        // S→D E, D→uno A E, A→dos B' tres (dos, B'→ε, tres), E→tres, luego E (de S)→tres
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("CASO 9: \"uno dos tres tres tres\"");
        System.out.println("═══════════════════════════════════════════════════════════════");
        Token[] caso9 = {Token.UNO, Token.DOS, Token.TRES, Token.TRES, Token.TRES};
        ASDR asdr9 = new ASDR(caso9);
        asdr9.analizar();
        asdr9.imprimirTraza();

        // Caso 10: Error - "cinco" (no válido)
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("CASO 10 (ERROR): \"cinco\"");
        System.out.println("═══════════════════════════════════════════════════════════════");
        Token[] caso10 = {Token.CINCO};
        ASDR asdr10 = new ASDR(caso10);
        asdr10.analizar();
        asdr10.imprimirTraza();

        // Resumen
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("                      RESUMEN DE RESULTADOS                   ");
        System.out.println("═══════════════════════════════════════════════════════════════");
        Object[][] casos = {
            {caso1, "ε (vacía)"}, {caso2, "dos tres"}, {caso3, "cuatro cinco"},
            {caso4, "seis"}, {caso5, "uno tres tres"}, {caso6, "dos cuatro seis cinco tres"},
            {caso7, "uno dos"}, {caso8, "tres"}, {caso9, "uno dos tres tres tres"},
            {caso10, "cinco"}
        };
        ASDR[] asdrs = {asdr1, asdr2, asdr3, asdr4, asdr5, asdr6, asdr7, asdr8, asdr9, asdr10};

        for (int i = 0; i < casos.length; i++) {
            List<String> t = asdrs[i].getTraza();
            String ultimaLinea = t.get(t.size() - 1);
            boolean exito = ultimaLinea.contains("exitoso");
            System.out.printf("  Caso %2d: %-35s → %s%n", i + 1, casos[i][1],
                exito ? "✓ ACEPTADA" : "✗ RECHAZADA");
        }
        System.out.println("═══════════════════════════════════════════════════════════════");
    }
}
