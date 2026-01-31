# Headings
## Valid
### ATX-Style
#### Pure
# Heading 1

## Heading 2

### Heading 3

#### Heading 4

##### Heading 5

###### Heading 6

#### Mixed
# *Heading 1*

## *Heading 2*

### *Heading 3*

#### *Heading 4*

##### *Heading 5*

###### *Heading 6*

### Setext-Style
#### Pure
Heading 1
=

Heading 1
===

Heading 2
-

Heading 2
---

#### Mixed
*Heading 1*
=

*Heading 2*
-

## Invalid
### ATX-Style
#### Escaped
\# Heading 1

\## Heading 2

\### Heading 3

\#### Heading 4

\##### Heading 5

\###### Heading 6

#### Malformed
#Heading 1

#- Heading 1

#Heading 2

#- Heading 2

#Heading 3

#- Heading 3

#Heading 4

#- Heading 4

#Heading 5

#- Heading 5

#Heading 6

#- Heading 6

### Setext-Style
#### Escaped
Heading 1
\=

Heading 2
\=

#### Malformed
Heading 1
=-

Heading 2
-=

---

# Bold
## Valid
### Single-Line
#### Pure
**Bold**

__Bold__

#### Mixed
***Bold***

**_Bold_**

*__Bold__*

__*Bold*__

### Multi-Line
#### Pure
**Bold line 1\
Bold line 2**

__Bold line 1\
Bold line 2__

#### Mixed
***Bold line 1\
Bold line 2***

**_Bold line 1\
Bold line 2_**

*__Bold line 1\
Bold line 2__*

__*Bold line 1\
Bold line 2*__

## Invalid
### Single-Line
#### Escaped
\*\*Bold**

**Bold\*\*

\_\_Bold__

__Bold\_\_

#### Malformed
*Bold**

**Bold*

**Bold__

_Bold__

__Bold_

__Bold**

### Multi-Line
#### Escaped
\*\*Bold line 1\
Bold line 2**

**Bold line 1\
Bold line 2\*\*

__Bold line 1\
Bold line 2\_\_

#### Malformed
*Bold line 1\
Bold line 2**

**Bold line 1\
Bold line 2*

**Bold line 1\
Bold line 2__

_Bold line 1\
Bold line 2__

__Bold line 1\
Bold line 2_

__Bold line 1\
Bold line 2**

---

# Italics
## Valid
### Single-Line
#### Pure
*Italics*

_Italics_

#### Mixed
***Italics***

**_Italics_**

*__Italics__*

__*Italics*__

### Multi-Line
#### Pure
*Italics line 1\
Italics line 2*

_Italics line 1\
Italics line 2_

#### Mixed
***Italics line 1\
Italics line 2***

**_Italics line 1\
Italics line 2_**

*__Italics line 1\
Italics line 2__*

__*Italics line 1\
Italics line 2*__

## Invalid
### Single-Line
#### Escaped
\*Italics*

*Italics\*

\_Italics_

_Italics\_

#### Malformed
*Italics

Italics*

### Multi-Line
#### Escaped
\*Italics line 1\
Italics line 2*

*Italics line 1\
Italics line 2\*

\_Italics line 1\
Italics line 2_

_Italics line 1\
Italics line 2\_

#### Malformed
*Italics line 1\
Italics line 2

Italics line 1\
Italics line 2*

# Line Break
## Valid
### Pure
Line 1  
Line 2

Line 1\
Line 2

### Mixed
*Line 1  
Line 2*

*Line 1\
Line 2*

## Invalid
### Escaped
Line 1\\
Line 2

### Malformed
Line 1\ 
Line 2

Line 1  Line 2

Line 1 
Line 2

---

# Block Quote
## Valid
### Single-Line
#### Standalone
##### Pure
> Block Quote

>Block Quote

##### Mixed
> *Block Quote*

>*Block Quote*

#### Nested
##### Pure
> > Nested Block Quote

>>Nested Block Quote

##### Mixed
> > *Block Quote*

>>*Block Quote*

### Multi-Line
#### Standalone
##### Pure
> Block Quote line 1\
> Block Quote line 2

>Block Quote line 1\
>Block Quote line 2

##### Mixed
> *Block Quote line 1\
> Block Quote line 2*

>*Block Quote line 1\
>Block Quote line 2*

#### Nested
#### Pure
> > Nested Block Quote line 1\
> > Nested Block Quote line 2

>>Nested Block Quote line 1\
>>Nested Block Quote line 2

#### Mixed
> > *Block Quote line 1\
> > Block Quote line 2*

>>*Block Quote line 1\
>>Block Quote line 2*

## Invalid
### Single-Line
#### Standalone
##### Escaped
\> Block Quote

\>Block Quote

##### Malformed
-> Block Quote

->Block Quote

#### Nested
##### Escaped
\> > Nested Block Quote

\>>Nested Block Quote

##### Malformed
->> Block Quote

->>Block Quote

### Multi-Line
#### Standalone
##### Escaped
\> Block Quote line 1
\> Block Quote line 2

\>Block Quote line 1
\>Block Quote line 2

##### Malformed
-> Block Quote line 1
-> Block Quote line 2

->Block Quote line 1
->Block Quote line 2

#### Nested
##### Escaped
\>> Nested Block Quote line 1
\>> Nested Block Quote line 2

\>>Nested Block Quote line 1
\>>Nested Block Quote line 2

##### Malformed
->> Nested Block Quote line 1
->> Nested Block Quote line 2

->>Nested Block Quote line 1
->>Nested Block Quote line 2

---

# Ordered List
## Valid
### Standalone
#### Pure
1. Ordered List item 1
2. Ordered List item 2


1) Ordered List item 1
2) Ordered List item 2


2. Ordered List item 1
1. Ordered List item 2


2) Ordered List item 1
1) Ordered List item 2

#### Mixed
1. *Ordered List item 1*
2. **Ordered List item 2**


1) *Ordered List item 1*
2) **Ordered List item 2**


2. *Ordered List item 1*
1. **Ordered List item 2**


2) *Ordered List item 1*
1) **Ordered List item 2**

### Nested
#### Pure
1. Ordered List item 1
   1. Ordered List item 2
      1. Ordered List item 3
2. Ordered List item 4
   1. Ordered List item 5
      1. Ordered List item 6

1) Ordered List item 1
   1) Ordered List item 2
      1) Ordered List item 3
2) Ordered List item 4
   1) Ordered List item 5
      1) Ordered List item 6

2. Ordered List item 1
   2. Ordered List item 2
      2. Ordered List item 3
1. Ordered List item 4
   2. Ordered List item 5
      2. Ordered List item 6

2) Ordered List item 1
   2) Ordered List item 2
      2) Ordered List item 3
1) Ordered List item 4
   2) Ordered List item 5
      2) Ordered List item 6

#### Mixed
1. *Ordered List item 1*
    1. **Ordered List item 2**
        1. *Ordered List item 3*
2. **Ordered List item 4**
    1. *Ordered List item 5*
        1. **Ordered List item 6**

1) *Ordered List item 1*
    1) **Ordered List item 2**
        1) *Ordered List item 3*
2) **Ordered List item 4**
    1) *Ordered List item 5*
        1) **Ordered List item 6**

2. *Ordered List item 1*
    2. **Ordered List item 2**
        2. *Ordered List item 3*
1. **Ordered List item 4**
    2. *Ordered List item 5*
        2. **Ordered List item 6**

2) *Ordered List item 1*
    2) **Ordered List item 2**
        2) *Ordered List item 3*
1) **Ordered List item 4**
    2) *Ordered List item 5*
        2) **Ordered List item 6**

## Invalid
### Standalone
#### Escaped
\1. Ordered List item 1
\2. Ordered List item 2


\1) Ordered List item 1
\2) Ordered List item 2


\2. Ordered List item 1
\1. Ordered List item 2


\2) Ordered List item 1
\1) Ordered List item 2

#### Malformed
1 Ordered List item 1
2 Ordered List item 2


1.Ordered List item 1
2.Ordered List item 2

### Nested
#### Escaped
\1. Ordered List item 1
    1. Ordered List item 2
        1. Ordered List item 3
\2. Ordered List item 4
    1. Ordered List item 5
        1. Ordered List item 6

1. Ordered List item 1
    \1. Ordered List item 2
        1. Ordered List item 3
2. Ordered List item 4
    \1. Ordered List item 5
        1. Ordered List item 6

1. Ordered List item 1
    1. Ordered List item 2
        \1. Ordered List item 3
2. Ordered List item 4
    1. Ordered List item 5
        \1. Ordered List item 6


\1) Ordered List item 1
    1) Ordered List item 2
        1) Ordered List item 3
\2) Ordered List item 4
    1) Ordered List item 5
        1) Ordered List item 6

1) Ordered List item 1
    \1) Ordered List item 2
        1) Ordered List item 3
2) Ordered List item 4
    \1) Ordered List item 5
        1) Ordered List item 6

1) Ordered List item 1
    1) Ordered List item 2
        \1) Ordered List item 3
2) Ordered List item 4
    1) Ordered List item 5
        \1) Ordered List item 6

#### Malformed
1- Ordered List item 4
    2. Ordered List item 5
        2. Ordered List item 6

1. Ordered List item 4
    2- Ordered List item 5
       2. Ordered List item 6

1. Ordered List item 4
   2. Ordered List item 5
      2- Ordered List item 6

---

# Unordered List
## Valid
### Standalone
#### Pure
- Unordered List item 1 
- Unordered List item 2

+ Unordered List item 1
+ Unordered List item 2

* Unordered List item 1
* Unordered List item 2

#### Mixed
- *Unordered List item 1*
- **Unordered List item 2**

+ *Unordered List item 1*
+ **Unordered List item 2**

* *Unordered List item 1*
* **Unordered List item 2**

### Nested
#### Pure
- Unordered List item 1
  - Unordered List item 2
    - Unordered List item 3
- Unordered List item 4
  - Unordered List item 5
    - Unordered List item 6

+ Unordered List item 1
  + Unordered List item 2
    + Unordered List item 3
+ Unordered List item 4 
  + Unordered List item 5
    + Unordered List item 6

* Unordered List item 1
  * Unordered List item 2
    * Unordered List item 3
* Unordered List item 4
  * Unordered List item 5
    * Unordered List item 6

#### Mixed
- *Unordered List item 1*
  - **Unordered List item 2**
    - *Unordered List item 3*
- **Unordered List item 4**
  - *Unordered List item 5*
    - **Unordered List item 6**

+ *Unordered List item 1*
  + **Unordered List item 2**
    + *Unordered List item 3*
+ **Unordered List item 4**
  + *Unordered List item 5*
    + **Unordered List item 6**

* *Unordered List item 1*
  * **Unordered List item 2**
    * *Unordered List item 3*
* **Unordered List item 4**
  * *Unordered List item 5*
    * **Unordered List item 6**

## Invalid
### Standalone
#### Escaped
\- Unordered List item 1
\- Unordered List item 2

\+ Unordered List item 1
\+ Unordered List item 2

\* Unordered List item 1
\* Unordered List item 2

#### Malformed
-| Unordered List item 1
-| Unordered List item 2

+| Unordered List item 1
+| Unordered List item 2

*| Unordered List item 1
*| Unordered List item 2

### Nested
#### Escaped
\- Unordered List item 1
    - Unordered List item 2
        - Unordered List item 3
\- Unordered List item 4
    - Unordered List item 5
        - Unordered List item 6

- Unordered List item 1
    \- Unordered List item 2
        - Unordered List item 3
- Unordered List item 4
    \- Unordered List item 5
        - Unordered List item 6

- Unordered List item 1
    - Unordered List item 2
        \- Unordered List item 3
- Unordered List item 4
    - Unordered List item 5
        \- Unordered List item 6

\+ Unordered List item 1
    + Unordered List item 2
        + Unordered List item 3
\+ Unordered List item 4
    + Unordered List item 5
        + Unordered List item 6

+ Unordered List item 1
    \+ Unordered List item 2
        + Unordered List item 3
+ Unordered List item 4
    \+ Unordered List item 5
        + Unordered List item 6

+ Unordered List item 1
    + Unordered List item 2
        \+ Unordered List item 3
+ Unordered List item 4
    + Unordered List item 5
        \+ Unordered List item 6

\* Unordered List item 1
    * Unordered List item 2
        * Unordered List item 3
\* Unordered List item 4
    * Unordered List item 5
        * Unordered List item 6

* Unordered List item 1
    \* Unordered List item 2
        * Unordered List item 3
* Unordered List item 4
    \* Unordered List item 5
        * Unordered List item 6

* Unordered List item 1
    * Unordered List item 2
        \* Unordered List item 3
* Unordered List item 4
    * Unordered List item 5
        \* Unordered List item 6

#### Malformed
-| Unordered List item 1
    - Unordered List item 2
        - Unordered List item 3 

- Unordered List item 1
    -| Unordered List item 2
        - Unordered List item 3

- Unordered List item 1
    - Unordered List item 2
        -| Unordered List item 3

---

# Inline Code
## Valid


## Invalid

---

# Code Block
## Valid


## Invalid

---

# Thematic Break
## Valid


## Invalid

---

# Link
## Valid


## Invalid
