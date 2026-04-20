#!/bin/bash

# Vaccine Appointment System Start Script
# This script provides multiple ways to start the application

echo "========================================="
echo "Vaccine Appointment System"
echo "========================================="

# Check if Docker Compose is available
if command -v docker-compose &> /dev/null || command -v docker compose &> /dev/null; then
    echo "Docker Compose detected"
    echo ""
    echo "Available options:"
    echo "1. Start with Docker Compose (MySQL + Redis + App)"
    echo "2. Start Spring Boot app only (requires external MySQL/Redis)"
    echo "3. Exit"
    echo ""
    read -p "Enter your choice (1-3): " choice

    case $choice in
        1)
            echo "Starting with Docker Compose..."
            docker-compose up --build
            ;;
        2)
            echo "Starting Spring Boot app only..."
            echo "Make sure MySQL and Redis are running"
            mvn spring-boot:run
            ;;
        3)
            echo "Exiting..."
            exit 0
            ;;
        *)
            echo "Invalid choice. Exiting..."
            exit 1
            ;;
    esac
else
    echo "Docker Compose not found. Starting Spring Boot app only..."
    echo "Note: You need to have MySQL and Redis running separately"
    mvn spring-boot:run
fi