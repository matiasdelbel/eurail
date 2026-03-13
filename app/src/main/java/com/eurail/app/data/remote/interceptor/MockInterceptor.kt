package com.eurail.app.data.remote.interceptor

import com.eurail.app.data.remote.dto.ArticleDetailDto
import com.eurail.app.data.remote.dto.ArticleDto
import com.eurail.app.data.remote.dto.ArticleListDto
import com.eurail.app.data.remote.dto.ErrorDto
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import kotlin.random.Random

class MockInterceptor(
    private val errorProbability: Float = 0.15f
) : Interceptor {

    private val json = Json { 
        prettyPrint = true
        ignoreUnknownKeys = true 
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        if (shouldSimulateError()) {
            return createErrorResponse(chain)
        }

        return when {
            path.matches(Regex("/articles/?")) -> {
                createSuccessResponse(
                    chain = chain,
                    body = json.encodeToString(MockResponseData.articleListResponse)
                )
            }
            path.matches(Regex("/articles/([^/]+)/?")) -> {
                val articleId = path.split("/").last { it.isNotEmpty() }
                val article = MockResponseData.getArticleById(articleId)
                if (article != null) {
                    createSuccessResponse(
                        chain = chain,
                        body = json.encodeToString(article)
                    )
                } else {
                    createNotFoundResponse(chain, articleId)
                }
            }
            else -> {
                createSuccessResponse(
                    chain = chain,
                    body = """{"message": "Mock endpoint not implemented: $path"}""",
                    code = 501
                )
            }
        }
    }

    private fun shouldSimulateError(): Boolean {
        return Random.nextFloat() < errorProbability
    }

    private fun createSuccessResponse(
        chain: Interceptor.Chain,
        body: String,
        code: Int = 200
    ): Response {
        return Response.Builder()
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(httpStatusMessage(code))
            .header("Content-Type", "application/json")
            .body(body.toResponseBody("application/json".toMediaType()))
            .build()
    }

    private fun createErrorResponse(chain: Interceptor.Chain): Response {
        return when (Random.nextInt(4)) {
            0 -> createNetworkErrorResponse(chain, 503, "Service Unavailable")
            1 -> createNetworkErrorResponse(chain, 504, "Gateway Timeout")
            2 -> createNetworkErrorResponse(chain, 500, "Internal Server Error")
            else -> createRateLimitResponse(chain)
        }
    }

    private fun createNetworkErrorResponse(
        chain: Interceptor.Chain,
        code: Int,
        message: String
    ): Response {
        val errorBody = json.encodeToString(
            ErrorDto(
                errorCode = "SERVER_ERROR",
                errorTitle = message,
                errorMessage = "The server encountered an error. Please try again later."
            )
        )
        return Response.Builder()
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(message)
            .body(errorBody.toResponseBody("application/json".toMediaType()))
            .header("Content-Type", "application/json")
            .build()
    }

    private fun createRateLimitResponse(chain: Interceptor.Chain): Response {
        val errorBody = json.encodeToString(
            ErrorDto(
                errorCode = "RATE_LIMIT_EXCEEDED",
                errorTitle = "Too Many Requests",
                errorMessage = "Please wait a moment before trying again."
            )
        )
        return Response.Builder()
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .code(429)
            .message("Too Many Requests")
            .body(errorBody.toResponseBody("application/json".toMediaType()))
            .header("Content-Type", "application/json")
            .build()
    }

    private fun createNotFoundResponse(chain: Interceptor.Chain, articleId: String): Response {
        val errorBody = json.encodeToString(
            ErrorDto(
                errorCode = "ARTICLE_NOT_FOUND",
                errorTitle = "Article Not Found",
                errorMessage = "The requested article with ID '$articleId' could not be found."
            )
        )
        return Response.Builder()
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .code(404)
            .message("Not Found")
            .body(errorBody.toResponseBody("application/json".toMediaType()))
            .header("Content-Type", "application/json")
            .build()
    }

    private fun httpStatusMessage(code: Int): String = when (code) {
        200 -> "OK"
        201 -> "Created"
        204 -> "No Content"
        400 -> "Bad Request"
        401 -> "Unauthorized"
        403 -> "Forbidden"
        404 -> "Not Found"
        429 -> "Too Many Requests"
        500 -> "Internal Server Error"
        501 -> "Not Implemented"
        502 -> "Bad Gateway"
        503 -> "Service Unavailable"
        504 -> "Gateway Timeout"
        else -> "Unknown"
    }
}

private object MockResponseData {

    private val articles = listOf(
        ArticleDto(
            id = "1",
            title = "Getting Started with Your Rail Pass",
            summary = "Learn how to activate and use your Eurail pass for the first time.",
            content = """
Welcome to your European rail adventure! This guide will help you get started with your Eurail pass.

## Activation

Before your first journey, you need to activate your pass:

1. **Mobile Pass**: Open the Rail Planner app and follow the activation steps
2. **Paper Pass**: Fill in your passport details and travel dates

## First Journey

On your first train:
- Show your pass to the conductor when requested
- Some trains require **seat reservations** - check before boarding
- Night trains typically need supplements

## Tips for Success

- Download offline maps in the Rail Planner app
- Check schedules the day before travel
- Arrive at stations **15-20 minutes** early

> Pro tip: Major stations have information desks where staff speak English.

Happy travels!
            """.trimIndent(),
            updatedAt = "2024-03-10T14:30:00Z",
            category = "Getting Started"
        ),
        ArticleDto(
            id = "2",
            title = "Seat Reservations Explained",
            summary = "Understanding when and how to reserve seats on European trains.",
            content = """
Not all trains require reservations, but knowing which do can save you headaches.

## Mandatory Reservations

These trains **always** require reservations:
- High-speed trains (TGV, AVE, Frecciarossa)
- Night trains
- Scenic routes (Glacier Express, Bernina Express)

## How to Reserve

### Online
Use the Rail Planner app or national railway websites.

### At Stations
Visit the ticket office or use self-service machines.

### Costs
Reservation fees typically range from **€5-35** depending on the train type.

## Reservation-Free Travel

Regional trains and most intercity services in:
- Germany (except ICE Sprinter)
- Switzerland
- Austria
- Netherlands

| Country | High-Speed | Regional |
|---------|-----------|----------|
| France  | Required  | Free     |
| Spain   | Required  | Free     |
| Italy   | Required  | Free     |
            """.trimIndent(),
            updatedAt = "2024-03-08T09:15:00Z",
            category = "Planning"
        ),
        ArticleDto(
            id = "3",
            title = "Night Trains: A Complete Guide",
            summary = "Everything you need to know about overnight train travel in Europe.",
            content = """
Save time and accommodation costs by traveling while you sleep!

## Popular Routes

- **Nightjet** (Austria): Vienna ↔ Rome, Zurich, Hamburg
- **Intercités de Nuit** (France): Paris ↔ Nice, Toulouse
- **European Sleeper**: Brussels ↔ Prague, Berlin

## Accommodation Types

### Seat
- Most affordable option
- Reclines slightly
- Bring a travel pillow

### Couchette
- 4 or 6 beds per compartment
- Sheets and blanket provided
- Shared with other travelers

### Sleeper
- Private compartments (1-3 beds)
- Often includes breakfast
- En-suite bathroom in deluxe

## What's Included

✅ Bed linens
✅ Wake-up service
✅ Often: water bottle
❓ Breakfast (varies by service)

## Tips

1. Secure valuables near you
2. Use earplugs and eye mask
3. Book early for popular routes
4. Check passport requirements for border crossings
            """.trimIndent(),
            updatedAt = "2024-03-05T16:45:00Z",
            category = "Train Types"
        ),
        ArticleDto(
            id = "4",
            title = "Luggage Guidelines",
            summary = "What to pack and luggage restrictions on European trains.",
            content = """
European trains are generally flexible with luggage, but here's what you need to know.

## General Rules

- **No strict weight limits** on most trains
- Must store luggage yourself
- Should fit in overhead racks or designated areas

## Recommended Sizes

For comfortable travel:
- **Carry-on sized** bags work best
- Maximum suggested: 70x50x30 cm
- One large bag + one small bag is ideal

## Storage Options

### Overhead Racks
- Best for smaller bags
- Keep valuables with you

### End of Car
- Designated luggage areas
- Keep an eye on bags at stops

### Under Seats
- Limited space
- Good for day bags

## High-Speed Trains

Some trains (Eurostar, Thalys) have:
- Security screening
- Size recommendations
- No prohibited items

## Left Luggage

Most major stations offer:
- Lockers (€3-10 per day)
- Staffed luggage storage
- Various size options
            """.trimIndent(),
            updatedAt = "2024-03-01T11:20:00Z",
            category = "Practical Info"
        ),
        ArticleDto(
            id = "5",
            title = "Best Scenic Train Routes",
            summary = "Discover the most breathtaking railway journeys across Europe.",
            content = """
Europe offers some of the world's most spectacular train journeys.

## Switzerland

### Glacier Express
*Zermatt ↔ St. Moritz*
- 8 hours of Alpine splendor
- Crosses 291 bridges
- Goes through 91 tunnels
- **Reservation required**

### Bernina Express
*Chur ↔ Tirano*
- UNESCO World Heritage route
- Reaches 2,253m altitude
- Stunning viaducts

## Norway

### Bergen Railway
*Oslo ↔ Bergen*
- 7 hours through mountains
- Highest point: 1,222m
- Connect to Flåm Railway

## Austria

### Semmering Railway
*World's first mountain railway*
- UNESCO listed
- Historic viaducts
- Beautiful in all seasons

## Best Times

| Route | Season | Why |
|-------|--------|-----|
| Glacier Express | Summer | Clear views |
| Bergen Railway | Winter | Snow scenes |
| Cinque Terre | Spring | Flowers |

## Photography Tips

📸 Sit on the right side (Glacier Express)
📸 Clean the window before departure
📸 Use burst mode for bridges
            """.trimIndent(),
            updatedAt = "2024-02-28T13:00:00Z",
            category = "Inspiration"
        ),
        ArticleDto(
            id = "6",
            title = "Traveling with Children",
            summary = "Tips for a smooth family rail journey across Europe.",
            content = """
Rail travel is excellent for families. Here's how to make it even better.

## Age Policies

- **Under 4**: Usually free, no seat
- **4-11**: Often 50% discount
- **12+**: Adult fare (Eurail Youth available under 28)

## Family-Friendly Features

### On Board
- Changing tables in accessible toilets
- Family compartments (reserve early)
- Play areas on some trains
- Restaurant/café cars

### At Stations
- Family waiting rooms
- Play areas
- Elevators and ramps

## Packing Essentials

For train journeys with kids:

- [ ] Snacks (lots of them!)
- [ ] Entertainment (tablets, books, games)
- [ ] Headphones
- [ ] Change of clothes
- [ ] Wet wipes
- [ ] Favorite toy/comfort item

## Booking Tips

1. **Book family compartments** where available
2. **Choose off-peak** times when possible
3. **Allow buffer time** between connections
4. **Seat reservations** reduce stress

## Entertainment Ideas

Keep little ones busy:
- Train bingo (spot tunnels, bridges, animals)
- Window counting games
- Coloring books with train themes
- Audio stories

> The journey is part of the adventure!
            """.trimIndent(),
            updatedAt = "2024-02-25T10:30:00Z",
            category = "Practical Info"
        )
    )

    val articleListResponse = ArticleListDto(articles = articles)

    fun getArticleById(id: String): ArticleDetailDto? = articles.find { it.id == id }?.toArticleDetailDto()

    private fun ArticleDto.toArticleDetailDto() = ArticleDetailDto(
        id = id,
        title = title,
        summary = summary,
        content = content,
        updatedAt = updatedAt,
        category = category
    )
}
