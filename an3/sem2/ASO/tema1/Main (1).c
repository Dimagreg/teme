#include <windows.h>
#include <stdio.h>
#include <string.h>

#ifdef UNICODE
#define RegOpenKeyEx RegOpenKeyExW
#define RegQueryValueEx RegQueryValueExW
#else
#define RegOpenKeyEx RegOpenKeyExA
#define RegQueryValueEx RegQueryValueExA
#endif

#define MAX_KEY_LENGTH 255
#define MAX_VALUE_NAME 32767
#define MAX_VALUE_SIZE 4096

// Function to read and print registry values, opening the registry key internally
void ReadRegistryValues(HKEY hBaseKey, LPCTSTR subKey) {
    HKEY hKey;
    LSTATUS result;

    result = RegOpenKeyEx(hBaseKey,
        subKey,
        0,
        KEY_READ,
        &hKey);

    if (result != ERROR_SUCCESS) {
        printf("Failed to open registry key. Error code: %ld\n", result);
        return;
    }

    DWORD cSubKeys = 0;               // number of subkeys 
    DWORD cValues = 0;                // number of values for key 
    DWORD i = 0, retCode = 0;

    retCode = RegQueryInfoKey(
        hKey,                    // key handle 
        NULL,                     // buffer for class name 
        NULL,                     // size of class string 
        NULL,                     // reserved 
        &cSubKeys,                // number of subkeys 
        NULL,                     // longest subkey size 
        NULL,                     // longest class string 
        &cValues,                 // number of values for this key 
        NULL,                     // longest value name 
        NULL,                     // longest value data 
        NULL,                     // security descriptor 
        NULL);                    // last write time 

    if (retCode != ERROR_SUCCESS) {
        printf("Failed to query registry info. Error code: %ld\n", retCode);
        RegCloseKey(hKey);
        return;
    }

    printf("\nNumber of values: %d\n", cValues);

    DWORD cchValue = MAX_VALUE_NAME;
    TCHAR achValue[MAX_VALUE_NAME] = { '\0' };

    DWORD type = 0;
    wchar_t lpData[MAX_VALUE_SIZE];
    DWORD bufferSize = MAX_VALUE_SIZE;

    for (i = 0; i < cValues; i++) {
        cchValue = MAX_VALUE_NAME;
        achValue[0] = '\0';

        result = RegEnumValue(hKey, i,
            achValue,
            &cchValue,
            NULL,
            &type,
            (LPBYTE)lpData,
            &bufferSize);

        printf("(%d) %ws\n", i + 1, achValue);

        if (result == ERROR_SUCCESS) {
            if (type == REG_SZ) {
                printf("(%d) path: %ws\n", i + 1, lpData);
            }
            else if (type == REG_DWORD) {
                printf("Value is a DWORD: %ld\n", *(DWORD*)lpData);
            }
            else if (type == REG_EXPAND_SZ) {
                printf("(%d) path: %ws\n", i + 1, lpData);
            }
            else {
                printf("Unknown type: %ld\n", type);
            }
        }
        else {
            printf("Failed to query the value. Error code: %ld\n", result);
        }

        bufferSize = MAX_VALUE_SIZE; // Reset buffer size for next iteration
    }

    RegCloseKey(hKey);  // Close the registry key after processing
}

int main() {
    ReadRegistryValues(HKEY_CURRENT_USER, TEXT("Software\\Microsoft\\Windows\\CurrentVersion\\Run"));

    ReadRegistryValues(HKEY_LOCAL_MACHINE, TEXT("Software\\Microsoft\\Windows\\CurrentVersion\\Run"));

    return 0;
}
