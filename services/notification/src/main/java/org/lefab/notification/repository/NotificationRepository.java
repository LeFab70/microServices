package org.lefab.notification.repository;

import org.lefab.notification.entities.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface NotificationRepository extends MongoRepository<Notification, String> {}