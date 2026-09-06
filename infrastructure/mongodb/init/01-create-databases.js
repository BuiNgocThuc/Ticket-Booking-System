db = db.getSiblingDB("customer_db");
db.createUser({
    user: "customer_user",
    pwd: "ticket_dev",
    roles: [
        {
            role: "readWrite",
            db: "customer_db"
        }
    ]
});

db = db.getSiblingDB("notification_db");
db.createUser({
    user: "notification_user",
    pwd: "ticket_dev",
    roles: [
        {
            role: "readWrite",
            db: "notification_db"
        }
    ]
});