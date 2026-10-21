# Called by Play.cmd before the game starts. Never downloads or executes
# remote code: it only extracts the release archive published by this fork.
# All user data is stored in userdata/, which is not present in release zips.
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$repo = 'WillowWLJPearl/willowforgefork'
$assetName = 'WillowForge-Windows.zip'
$temp = $null

try {
    $headers = @{ 'User-Agent' = 'WillowForge-Portable-Updater'; 'Accept' = 'application/vnd.github+json' }
    $release = Invoke-RestMethod -Uri "https://api.github.com/repos/$repo/releases/latest" -Headers $headers -TimeoutSec 12
    $latest = [string]$release.tag_name
    if ([string]::IsNullOrWhiteSpace($latest)) { return }

    $tagPath = Join-Path $root 'BUILD_TAG.txt'
    if ((Test-Path -LiteralPath $tagPath) -and ((Get-Content -Raw -LiteralPath $tagPath).Trim() -eq $latest)) {
        return
    }

    $asset = @($release.assets | Where-Object { $_.name -eq $assetName }) | Select-Object -First 1
    if (-not $asset) { return }

    Write-Host "Updating WillowForge to $latest..."
    $temp = Join-Path ([System.IO.Path]::GetTempPath()) ('WillowForge-' + [guid]::NewGuid().ToString('N'))
    New-Item -Path $temp -ItemType Directory -Force | Out-Null
    $zip = Join-Path $temp $assetName
    Invoke-WebRequest -Uri $asset.browser_download_url -OutFile $zip -UseBasicParsing -TimeoutSec 240
    $extracted = Join-Path $temp 'unpacked'
    Expand-Archive -LiteralPath $zip -DestinationPath $extracted -Force

    $newRoot = Join-Path $extracted 'WillowForge'
    if (-not (Test-Path -LiteralPath (Join-Path $newRoot 'runtime\bin\javaw.exe'))) {
        throw 'Invalid release archive: missing Java runtime'
    }
    if (-not (Test-Path -LiteralPath (Join-Path $newRoot 'Play.cmd'))) {
        throw 'Invalid release archive: missing launcher'
    }
    if (-not (Test-Path -LiteralPath (Join-Path $newRoot 'app'))) {
        throw 'Invalid release archive: missing NeoForge application'
    }

    # Only files from the official release are overlaid. userdata/ is untouched.
    Copy-Item -LiteralPath (Join-Path $newRoot '*') -Destination $root -Force -Recurse
    Set-Content -LiteralPath $tagPath -Encoding Ascii -Value $latest
    Write-Host "Updated to $latest."
} catch {
    # Offline use must always work; failure to update must not block playing.
    Write-Warning ("Update check skipped: " + $_.Exception.Message)
} finally {
    if ($temp -and (Test-Path -LiteralPath $temp)) {
        Remove-Item -LiteralPath $temp -Recurse -Force -ErrorAction SilentlyContinue
    }
}
