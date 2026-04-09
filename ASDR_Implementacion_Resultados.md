# Implementación ASDR - Ejercicio 1, Presentación 07
## Analizador Sintáctico Descendente Recursivo

---

## Gramática Transformada (sin recursividad por la izquierda)

```
S  → A B' C        |  S  → D E
A  → dos B' tres   |  A  → ε
B' → cuatro C cinco B'  |  B' → ε
C  → seis A B'     |  C  → ε
D  → uno A E       |  D  → B'
E  → tres
```

> **Nota:** La gramática NO es LL(1) debido al conflicto en S con el terminal `cuatro`. En la implementación se resuelve eligiendo `S → A B' C` cuando el token actual es `cuatro` (ya que `A → ε` permite que `B'` lo consuma).

---


---

## Resultados de Ejecución

### Caso 1: `ε` (cadena vacía) — ✓ ACEPTADA

Derivación: `S → A B' C → ε ε ε → ε`

```
S() → token actual: '$'
  ↳ Regla: S → A B' C
A() → token actual: '$'
  ↳ Regla: A → ε
B'() → token actual: '$'
  ↳ Regla: B' → ε
C() → token actual: '$'
  ↳ Regla: C → ε
✓ Análisis EXITOSO
```

---

### Caso 2: `dos tres` — ✓ ACEPTADA

Derivación: `S → A B' C → dos B' tres B' C → dos ε tres ε ε → dos tres`

```
S() → token actual: 'dos'
  ↳ Regla: S → A B' C
A() → token actual: 'dos'
  ↳ Regla: A → dos B' tres
  ✓ emparejar('dos')
B'() → token actual: 'tres'
  ↳ Regla: B' → ε
  ✓ emparejar('tres')
B'() → token actual: '$'
  ↳ Regla: B' → ε
C() → token actual: '$'
  ↳ Regla: C → ε
✓ Análisis EXITOSO
```

---

### Caso 3: `cuatro cinco` — ✓ ACEPTADA

Derivación: `S → A B' C → ε (cuatro C cinco B') C → cuatro ε cinco ε ε → cuatro cinco`

```
S() → token actual: 'cuatro'
  ↳ Regla: S → A B' C
A() → token actual: 'cuatro'
  ↳ Regla: A → ε
B'() → token actual: 'cuatro'
  ↳ Regla: B' → cuatro C cinco B'
  ✓ emparejar('cuatro')
C() → token actual: 'cinco'
  ↳ Regla: C → ε
  ✓ emparejar('cinco')
B'() → token actual: '$'
  ↳ Regla: B' → ε
C() → token actual: '$'
  ↳ Regla: C → ε
✓ Análisis EXITOSO
```

---

### Caso 4: `seis` — ✓ ACEPTADA

Derivación: `S → A B' C → ε ε (seis A B') → seis ε ε → seis`

```
S() → token actual: 'seis'
  ↳ Regla: S → A B' C
A() → token actual: 'seis'
  ↳ Regla: A → ε
B'() → token actual: 'seis'
  ↳ Regla: B' → ε
C() → token actual: 'seis'
  ↳ Regla: C → seis A B'
  ✓ emparejar('seis')
A() → token actual: '$'
  ↳ Regla: A → ε
B'() → token actual: '$'
  ↳ Regla: B' → ε
✓ Análisis EXITOSO
```

---

### Caso 5: `uno tres tres` — ✓ ACEPTADA

Derivación: `S → D E → (uno A E) E → uno ε tres tres → uno tres tres`

```
S() → token actual: 'uno'
  ↳ Regla: S → D E
D() → token actual: 'uno'
  ↳ Regla: D → uno A E
  ✓ emparejar('uno')
A() → token actual: 'tres'
  ↳ Regla: A → ε
E() → token actual: 'tres'
  ↳ Regla: E → tres
  ✓ emparejar('tres')
E() → token actual: 'tres'
  ↳ Regla: E → tres
  ✓ emparejar('tres')
✓ Análisis EXITOSO
```

---

### Caso 6: `dos cuatro seis cinco tres` — ✓ ACEPTADA

Derivación: `S → A B' C → (dos B' tres) B' C → dos (cuatro C cinco B') tres B' C → dos cuatro (seis A B') cinco ε tres ε ε → dos cuatro seis ε ε cinco tres → dos cuatro seis cinco tres`

```
S() → token actual: 'dos'
  ↳ Regla: S → A B' C
A() → token actual: 'dos'
  ↳ Regla: A → dos B' tres
  ✓ emparejar('dos')
B'() → token actual: 'cuatro'
  ↳ Regla: B' → cuatro C cinco B'
  ✓ emparejar('cuatro')
C() → token actual: 'seis'
  ↳ Regla: C → seis A B'
  ✓ emparejar('seis')
A() → token actual: 'cinco'
  ↳ Regla: A → ε
B'() → token actual: 'cinco'
  ↳ Regla: B' → ε
  ✓ emparejar('cinco')
B'() → token actual: 'tres'
  ↳ Regla: B' → ε
  ✓ emparejar('tres')
B'() → token actual: '$'
  ↳ Regla: B' → ε
C() → token actual: '$'
  ↳ Regla: C → ε
✓ Análisis EXITOSO
```

---

### Caso 7: `tres` — ✓ ACEPTADA

Derivación: `S → D E → B' E → ε tres → tres`

```
S() → token actual: 'tres'
  ↳ Regla: S → D E
D() → token actual: 'tres'
  ↳ Regla: D → B'
B'() → token actual: 'tres'
  ↳ Regla: B' → ε
E() → token actual: 'tres'
  ↳ Regla: E → tres
  ✓ emparejar('tres')
✓ Análisis EXITOSO
```

---

### Caso 8: `uno dos tres tres tres` — ✓ ACEPTADA

Derivación: `S → D E → (uno A E) E → uno (dos B' tres) E E → uno dos ε tres tres tres`

```
S() → token actual: 'uno'
  ↳ Regla: S → D E
D() → token actual: 'uno'
  ↳ Regla: D → uno A E
  ✓ emparejar('uno')
A() → token actual: 'dos'
  ↳ Regla: A → dos B' tres
  ✓ emparejar('dos')
B'() → token actual: 'tres'
  ↳ Regla: B' → ε
  ✓ emparejar('tres')
E() → token actual: 'tres'
  ↳ Regla: E → tres
  ✓ emparejar('tres')
E() → token actual: 'tres'
  ↳ Regla: E → tres
  ✓ emparejar('tres')
✓ Análisis EXITOSO
```

---

### Caso 9: `cuatro seis cinco cinco` — ✗ RECHAZADA

```
S() → token actual: 'cuatro'
  ↳ Regla: S → A B' C
A() → token actual: 'cuatro'
  ↳ Regla: A → ε
B'() → token actual: 'cuatro'
  ↳ Regla: B' → cuatro C cinco B'
  ✓ emparejar('cuatro')
C() → token actual: 'seis'
  ↳ Regla: C → seis A B'
  ✓ emparejar('seis')
A() → token actual: 'cinco'
  ↳ Regla: A → ε
B'() → token actual: 'cinco'
  ↳ Regla: B' → ε
  ✓ emparejar('cinco')
B'() → token actual: 'cinco'
  ↳ Regla: B' → ε
C() → token actual: 'cinco'
  ↳ Regla: C → ε
✗ Error: Se esperaba '$' pero se encontró 'cinco' (posición 3)
```

---

### Caso 10: `uno dos` — ✗ RECHAZADA

```
S() → token actual: 'uno'
  ↳ Regla: S → D E
D() → token actual: 'uno'
  ↳ Regla: D → uno A E
  ✓ emparejar('uno')
A() → token actual: 'dos'
  ↳ Regla: A → dos B' tres
  ✓ emparejar('dos')
B'() → token actual: '$'
  ↳ Regla: B' → ε
✗ Error: Se esperaba 'tres' pero se encontró '$' (posición 2)
```

---

### Caso 11: `cinco` — ✗ RECHAZADA

```
S() → token actual: 'cinco'
✗ Error: Se esperaba 'dos' o 'cuatro' o 'seis' o 'uno' o 'tres' o '$'
         pero se encontró 'cinco' (posición 0)
```

---

### Caso 12: `dos cuatro` — ✗ RECHAZADA

```
S() → token actual: 'dos'
  ↳ Regla: S → A B' C
A() → token actual: 'dos'
  ↳ Regla: A → dos B' tres
  ✓ emparejar('dos')
B'() → token actual: 'cuatro'
  ↳ Regla: B' → cuatro C cinco B'
  ✓ emparejar('cuatro')
C() → token actual: '$'
  ↳ Regla: C → ε
✗ Error: Se esperaba 'cinco' pero se encontró '$' (posición 2)
```

---

## Resumen de Resultados

| Caso | Cadena | Resultado |
|:----:|--------|:---------:|
| 1 | `ε` (vacía) | ✓ Aceptada |
| 2 | `dos tres` | ✓ Aceptada |
| 3 | `cuatro cinco` | ✓ Aceptada |
| 4 | `seis` | ✓ Aceptada |
| 5 | `uno tres tres` | ✓ Aceptada |
| 6 | `dos cuatro seis cinco tres` | ✓ Aceptada |
| 7 | `tres` | ✓ Aceptada |
| 8 | `uno dos tres tres tres` | ✓ Aceptada |
| 9 | `cuatro seis cinco cinco` | ✗ Rechazada |
| 10 | `uno dos` | ✗ Rechazada |
| 11 | `cinco` | ✗ Rechazada |
| 12 | `dos cuatro` | ✗ Rechazada |

**Total: 8 aceptadas, 4 rechazadas de 12 casos.**

---

## Cómo Ejecutar

```bash
python3 asdr.py
```
