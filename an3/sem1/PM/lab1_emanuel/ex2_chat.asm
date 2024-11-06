.model small
.stack 100h

.data
    promptA db "Enter value for A (0-9): $"
    promptB db "Enter value for B (0-9): $"
    promptC db "Enter value for C (1-9): $"
    resultMsg db "Result: $"
    newline db 13, 10, "$"
    
    A db 0          ; Variable to store A
    B db 0          ; Variable to store B
    C db 0          ; Variable to store C
    result db 0     ; Variable to store result
    
.code
main:
    ; Read A
    lea dx, promptA
    mov ah, 09h
    int 21h
    call read_digit
    mov A, al       ; Store A

    ; Read B
    lea dx, promptB
    mov ah, 09h
    int 21h
    call read_digit
    mov B, al       ; Store B

    ; Read C
    lea dx, promptC
    mov ah, 09h
    int 21h
    call read_digit
    mov C, al       ; Store C

    ; Calculate (2A - 3B) / (5C)
    mov al, A       ; Load A into AL
    sub al, '0'     ; Convert ASCII to integer
    shl al, 1       ; AL = 2A

    mov bl, B       ; Load B into BL
    sub bl, '0'     ; Convert ASCII to integer
    shl bl, 1       ; BL = 2B
    sub al, bl      ; AL = 2A - 2B

    mov bl, B       ; Load B into BL again
    sub bl, '0'     ; Convert ASCII to integer
    add bl, B       ; BL = 2B
    add bl, B       ; BL = 3B
    sub al, bl      ; AL = 2A - 3B

    mov bl, C       ; Load C into BL
    sub bl, '0'     ; Convert ASCII to integer
    shl bl, 2       ; BL = 4C
    add bl, C       ; BL = 5C

    ; Perform division
    xor ah, ah      ; Clear AH for division
    div bl          ; AL = (2A - 3B) / (5C)
    add al, '0'     ; Convert result back to ASCII
    mov result, al  ; Store result

    ; Print result
    lea dx, resultMsg
    mov ah, 09h
    int 21h
    mov dl, result   ; Load result to DL
    mov ah, 02h      ; Function to print character
    int 21h

    ; New line
    lea dx, newline
    mov ah, 09h
    int 21h

    ; Exit program
    mov ax, 4C00h    ; Terminate program
    int 21h

; Function to read a single digit from input
read_digit:
    mov ah, 01h      ; Function to read character
    int 21h
    ret

end main
