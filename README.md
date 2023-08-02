# NotificationCalling-android
NotificationCalling

**Step 1** Download Google-Service from your firebase project and add to folder app.

**Step 2** Calling postman service https://fcm.googleapis.com/fcm/send set method POST.

**Step 3** Add Headers key is "Authorization" and your value is "key=YOUR_SERVER_KEY".

**Step 4** Add Body style raw like this.

```json

{
  "data": {
    "type": "call_windowNotification",
    "data": {
        "name": "Demo",
        "room": "Room_id"
    }
  },
  "mutable_content": true,
  "priority": "high",
  "registration_ids": ["YOUR_FCM_KEY"]
}


```

