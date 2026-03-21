# Conceptual Roadmap for Image Uploads: "A Three-Step Tango"

Whenever a controller needs an image (e.g., Course Banners, Course Thumbnails, Portfolio Trainers), it relies on the Three-Step Flow.

Instead of treating the backend as a middleman for heavy files, the backend now acts like a bouncer giving out VIP passes.

---

## 1. The Bouncer (Get Upload Signature)

**Goal:** Get a secure token/URL that grants permission to upload directly to your Cloud Storage.

- **Who calls this?** Frontend  
- **Where does it go?**  
    `GET /api/v1/storage/upload-signature?folder=<any_folder>`
- **What you get back:**  
    JSON payload with:
    - `api_key`
    - `timestamp`
    - `signature`
    - `uploadUrl`

---

## 2. The Delivery (Direct Upload to Cloudinary/S3)

**Goal:** Put the heavy image where it belongs without touching the Spring Boot internal memory.

- **Who calls this?** Frontend  
- **Where does it go?**  
    Directly to the Cloudinary URL from Step 1 (e.g., `https://api.cloudinary.com/v1_1/dcurn2sr2/auto/upload`)
- **What you attach:**  
    - The image file as `multipart/form-data`
    - All the puzzle pieces from Step 1 (`api_key`, `timestamp`, `signature`, `folder`)
- **What you get back:**  
    Cloudinary returns a JSON object. The only part you care about is the final web URL of the image: `secure_url`.

---

## 3. The Registration (Confirm via your Controllers)

**Goal:** The image is now safely in the cloud, but the Lamicons PostgreSQL database has no idea it exists. You must save the URL!

- **Who calls this?** Frontend  
- **Where does it go?** Your specific logic controllers:
    - **Course Banner:**  
        `POST /api/v1/courses/files/{uuid}/banner/confirm`
    - **Portfolio Trainer:**  
        `POST /api/v1/portfolio/trainers`
- **What you attach:**  
    A simple JSON body with the URL you got from Cloudinary in Step 2.

---

# Step-by-Step Testing Guide in Postman

Now, let's map this to what you actually click inside Postman so you can add test evidence to your Google Sheet!

---

## Scenario 1: Uploading a Course Banner

### Step 1: Get the Signature

```http
GET http://localhost:8082/api/v1/storage/upload-signature?folder=courses/banners
```
- Copy `signature`, `timestamp`, `apiKey`, and `uploadUrl` from the response.

### Step 2: Upload to Cloudinary

```http
POST [Paste the uploadUrl here]
```
- Go to the **Body** tab → select **form-data**.
- Add the following keys:
    - `file` (set type to File): Select an image from your PC.
    - `api_key`: [from Step 1]
    - `timestamp`: [from Step 1]
    - `signature`: [from Step 1]
    - `folder`: `courses/banners` (must match Step 1)
- Hit **SEND**.
- Copy the `secure_url` from the Cloudinary JSON response.

### Step 3: Save to Lamicons Database

```http
POST http://localhost:8082/api/v1/courses/files/c992e52a-977a-4936-b8c5-e13fe8f475ed/banner/confirm
```
- Go to the **Body** tab → select **raw** → select **JSON**.
- Paste the following:

```json
{
    "url": "[secure_url from Step 2]"
}
```
- Hit **SEND**. (Your DB is now updated!)

---

## Scenario 2: Adding a Featured Trainer (Portfolio)

### Step 1: Get the Signature

```http
GET http://localhost:8082/api/v1/storage/upload-signature?folder=portfolio/trainers
```

### Step 2: Upload the Profile Picture to Cloudinary

- Use the same Postman setup as previous Step 2, just make sure `folder` matches `portfolio/trainers`.
- Copy the `secure_url` of the trainer's profile picture from Cloudinary's response.

### Step 3: Create the Trainer in Lamicons Database

```http
POST http://localhost:8082/api/v1/portfolio/trainers
```
- Go to the **Body** tab → select **raw** → select **JSON**.
- Paste the following:

```json
{
    "profileImageUrl": "[secure_url from Step 2]",
    // ...other trainer fields
}
```
- Hit **SEND**. (The Portfolio database row is now created with the new Cloudinary image included!)

---

# How to Explain This to the Frontend Team

Just tell them:

> "Hey frontend team, our backend no longer accepts raw image files. Whenever you need to upload an image across the application, you must first hit `/api/v1/storage/upload-signature` to get your Cloudinary credentials. Upload the file securely to Cloudinary using those credentials, extract the final URL they give you, and then push that string URL to our backend endpoints."

This roadmap proves that every controller requiring an image simply expects the final URL string! It does **not** care about actual image files anymore—they only care about URLs.

---

You should now be perfectly equipped to run through your tests, your mentor's tests, and update the G-Sheet! Let your team know when you're ready to commit the changes.