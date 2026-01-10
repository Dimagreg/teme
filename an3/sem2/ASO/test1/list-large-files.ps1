# .\list-large-files.ps1
# PowerShell script to list files larger than a specified size threshold
# GRIGORI DMITRII
# 12.05.2025

param(
    [Parameter(Mandatory=$false, Position=0, 
               HelpMessage="The folder path to search for large files")]
    [ValidateScript({Test-Path $_ -PathType Container})]
    [string]$FolderPath = "C:\Test",
    
    [Parameter(Mandatory=$false, Position=1,
               HelpMessage="The size threshold in MB (default: 1MB)")]
    [ValidateRange(0.001, [double]::MaxValue)]
    [double]$SizeThresholdMB = 1,
    
    [Parameter(Mandatory=$false,
               HelpMessage="Display sizes in GB instead of MB")]
    [switch]$DisplayInGB = $false,
    
    [Parameter(Mandatory=$false,
               HelpMessage="Export results to CSV file")]
    [string]$ExportToCSV
)

# Calculate threshold in bytes
$sizeThreshold = $SizeThresholdMB * 1MB

# Check if folder exists (redundant with ValidateScript but kept for error clarity)
if (-not (Test-Path -Path $FolderPath -PathType Container)) {
    Write-Host "Error: The folder $FolderPath does not exist." -ForegroundColor Red
    exit
}

# Check if user has read access to the folder
try {
    $accessTest = [System.IO.Directory]::GetFiles($FolderPath, "*", [System.IO.SearchOption]::TopDirectoryOnly)
    Write-Host "Access check: You have read permissions to $FolderPath" -ForegroundColor Green
} 
catch [System.UnauthorizedAccessException] {
    Write-Host "Error: You do not have read permissions to $FolderPath" -ForegroundColor Red
    exit
}
catch {
    Write-Host "Error checking access permissions: $($_.Exception.Message)" -ForegroundColor Red
    exit
}

# Get all files in the folder and its subfolders
Write-Host "Searching for files larger than $SizeThresholdMB MB in $FolderPath..." -ForegroundColor Cyan

# Get files larger than the threshold and sort by size (largest first)
try {
    $largeFiles = Get-ChildItem -Path $FolderPath -File -Recurse -ErrorAction Stop -ErrorVariable accessErrors | 
                  Where-Object { $_.Length -gt $sizeThreshold } |
                  Sort-Object -Property Length -Descending
}
catch {
    Write-Host "Error: Failed to scan folder structure. $($_.Exception.Message)" -ForegroundColor Red
    exit
}

# Report any access errors encountered during scan
if ($accessErrors) {
    Write-Host "`nWarning: Some files or folders could not be accessed due to permission restrictions:" -ForegroundColor Yellow
    $accessErrors | ForEach-Object { 
        Write-Host "  - $($_.Exception.Message)" -ForegroundColor Yellow 
    }
    Write-Host ""
}

# Display results
if ($largeFiles.Count -eq 0) {
    Write-Host "No files larger than $SizeThresholdMB MB found in $FolderPath." -ForegroundColor Yellow
} else {
    Write-Host "Found $($largeFiles.Count) files larger than $SizeThresholdMB MB:" -ForegroundColor Green
    
    # Display the large files with formatted size
    $results = $largeFiles | ForEach-Object {
        if ($DisplayInGB) {
            $size = [math]::Round($_.Length / 1GB, 2)
            $unit = "GB"
        } else {
            $size = [math]::Round($_.Length / 1MB, 2)
            $unit = "MB"
        }
        
        $fileInfo = [PSCustomObject]@{
            FullPath = $_.FullName
            Name = $_.Name
            Size = $size
            Unit = $unit
            LastModified = $_.LastWriteTime
        }
        
        # Display to console
        Write-Host "$($_.FullName) - $size $unit"
        
        # Return the object for potential CSV export
        $fileInfo
    }
    
    # Export to CSV if specified
    if ($ExportToCSV) {
        try {
            $results | Export-Csv -Path $ExportToCSV -NoTypeInformation -ErrorAction Stop
            Write-Host "Results exported to $ExportToCSV" -ForegroundColor Green
        }
        catch {
            Write-Host "Error exporting to CSV: $($_.Exception.Message)" -ForegroundColor Red
        }
    }
}

# Display usage example at the end
Write-Host "`nUsage examples:" -ForegroundColor Cyan
Write-Host "  .\list-large-files.ps1 C:\Data 5" -ForegroundColor Cyan
Write-Host "  .\list-large-files.ps1 -FolderPath D:\Backups -SizeThresholdMB 100 -DisplayInGB" -ForegroundColor Cyan
Write-Host "  .\list-large-files.ps1 -FolderPath E:\Media -SizeThresholdMB 500 -ExportToCSV results.csv" -ForegroundColor Cyan