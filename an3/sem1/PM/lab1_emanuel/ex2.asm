dosseg

; (2A - 3B)/5C -> catul si restul

; A = 7
; B = 1
; C = 1
; cat = 2
; rest = 1

.model small

.stack 1000

.data
    A DB 0
    B DB 0
    C DB 0
    n1 DB "Introduceti primul numar: $"
    n2 DB "Introduceti al doilea numar: $"
    n3 DB "Introduceti al treilea numar: $"
    result_cat DB "Catul este: $"
    result_rest DB "Restul este: $"
    result DB "00$"

.code

new_line proc
    MOV AH, 2
    MOV DL, 10; ASCII code for new line
    INT 21h; DOS interrupt
    ret; return
new_line endp

main proc
    MOV AX, @data; initialize data segment
    MOV DS, AX; DS points to data segment
    
    ; read first number
    MOV AH, 9
    MOV DX, OFFSET n1; display message
    INT 21h; DOS interrupt
    MOV AH, 1; read character from keyboard
    INT 21h; DOS interrupt
    SUB AL, 30h; convert ASCII to digit
    MOV A, AL; save character in A
    CALL new_line; new line
    
    ; read second number
    MOV AH, 9
    MOV DX, OFFSET n2; display message
    INT 21h; DOS interrupt
    MOV AH, 1
    INT 21h
    SUB AL, 30h; convert ASCII to digit
    MOV B, AL
    CALL new_line

    ; read third number
    MOV AH, 9
    MOV DX, OFFSET n3; display message
    INT 21h; DOS interrupt
    MOV AH, 1
    INT 21h
    SUB AL, 30h; convert ASCII to digit
    MOV C, AL
    CALL new_line

    ; calculate
    MOV AL, A
    MOV BL, 2 
    MUL BL
    MOV BX, AX; (2 * A) -> BX
    
    MOV AL, B
    MOV BL, 3
    MUL BL;
    SUB BX, AX; (2A - 3B) -> BX

    MOV AL, C
    MOV BL, 5
    MUL BL; 
    MOV CX, AX; (5 * C) -> CX

    MOV DX, 0; clear DX
    MOV AX, BX
    DIV CX; AX / CX, AX - cat, DX - rest

    MOV BL, AL; cat
    MOV CL, DL; rest

    ; afiseaza catul
    MOV AH, 09h
    MOV DX, OFFSET result_cat
    INT 21h 

    MOV AL, BL
    ADD AL, '0'; convert digit to ASCII
    MOV DL, AL; move AL to DL
    MOV AH, 2
    INT 21h; DOS interrupt

    CALL new_line

    ; afiseaza restul
    MOV AH, 09h
    MOV DX, OFFSET result_rest
    INT 21h

    MOV AL, CL
    ADD AL, '0'; convert digit to ASCII
    MOV DL, AL; move AL to DL
    MOV AH, 2
    INT 21h; DOS interrupt

    ; Exit program
    MOV AH, 4Ch
    INT 21h
main endp

end main
