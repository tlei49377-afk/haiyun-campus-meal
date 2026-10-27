param(
    [string]$BaseUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"

function Invoke-JsonPost($Uri, $Body, $Headers = @{}) {
    $response = Invoke-RestMethod -Method Post -Uri $Uri -ContentType "application/json" `
        -Body ($Body | ConvertTo-Json -Depth 8) -Headers $Headers
    if ($response.code -ne 1) { throw "POST $Uri failed: $($response.msg)" }
    return $response
}

function Invoke-JsonPut($Uri, $Body = $null, $Headers = @{}) {
    if ($null -eq $Body) {
        $response = Invoke-RestMethod -Method Put -Uri $Uri -Headers $Headers
    } else {
        $response = Invoke-RestMethod -Method Put -Uri $Uri -ContentType "application/json" `
            -Body ($Body | ConvertTo-Json -Depth 8) -Headers $Headers
    }
    if ($response.code -ne 1) { throw "PUT $Uri failed: $($response.msg)" }
    return $response
}

function Invoke-JsonGet($Uri, $Headers = @{}) {
    $response = Invoke-RestMethod -Method Get -Uri $Uri -Headers $Headers
    if ($response.code -ne 1) { throw "GET $Uri failed: $($response.msg)" }
    return $response
}

$student = Invoke-JsonPost "$BaseUrl/api/app/user/login" @{ studentNo = "20230001"; password = "123456" }
$studentHeaders = @{ authentication = $student.data.token }
$canteens = Invoke-JsonGet "$BaseUrl/api/app/canteen/list" $studentHeaders
$stalls = Invoke-JsonGet "$BaseUrl/api/app/stall/list?canteenId=$($canteens.data[0].id)" $studentHeaders
$categories = Invoke-JsonGet "$BaseUrl/api/app/category/list?stallId=$($stalls.data[0].id)" $studentHeaders
$dishes = Invoke-JsonGet "$BaseUrl/api/app/dish/list?categoryId=$($categories.data[0].id)" $studentHeaders
$slotDate = Get-Date -Format "yyyy-MM-dd"
$slots = Invoke-JsonGet "$BaseUrl/api/app/slot/list?stallId=$($stalls.data[0].id)&slotDate=$slotDate" $studentHeaders
$slot = $slots.data | Where-Object { $_.remaining -gt 0 } | Select-Object -First 1
if (-not $slot) {
    $slotDate = (Get-Date).AddDays(1).ToString("yyyy-MM-dd")
    $slots = Invoke-JsonGet "$BaseUrl/api/app/slot/list?stallId=$($stalls.data[0].id)&slotDate=$slotDate" $studentHeaders
    $slot = $slots.data | Where-Object { $_.remaining -gt 0 } | Select-Object -First 1
}
if (-not $slot) { throw "近两日均没有可用取餐时段" }

$null = Invoke-JsonPost "$BaseUrl/api/app/shoppingCart/add" @{ dishId = $dishes.data[0].id } $studentHeaders
$order = Invoke-JsonPost "$BaseUrl/api/app/order/submit" @{ slotId = $slot.id; payMethod = 3; remark = "smoke test" } $studentHeaders
$null = Invoke-JsonPut "$BaseUrl/api/app/order/payment" @{ orderNumber = $order.data.orderNumber; payMethod = 3 } $studentHeaders

$admin = Invoke-JsonPost "$BaseUrl/api/admin/employee/login" @{ username = "admin"; password = "123456" }
$adminHeaders = @{ token = $admin.data.token }
$null = Invoke-JsonGet "$BaseUrl/api/admin/order/conditionSearch?page=1&pageSize=20" $adminHeaders
$null = Invoke-JsonPut "$BaseUrl/api/admin/order/confirm" @{ id = $order.data.id } $adminHeaders
$null = Invoke-JsonPut "$BaseUrl/api/admin/order/ready/$($order.data.id)" $null $adminHeaders
$verified = Invoke-JsonPost "$BaseUrl/api/admin/order/pickup/verify" @{ pickupCode = $order.data.pickupCode } $adminHeaders

[pscustomobject]@{
    Result = $verified.data.message
    OrderId = $order.data.id
    PickupCode = $order.data.pickupCode
    Amount = $order.data.orderAmount
    Slot = "$($slot.startTime)-$($slot.endTime)"
} | Format-List
