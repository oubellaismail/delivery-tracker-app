output "staging_ip" {
  value       = digitalocean_droplet.staging.ipv4_address
  description = "Public IP of staging droplet"
}

output "production_ip" {
  value       = digitalocean_droplet.production.ipv4_address
  description = "Public IP of production droplet"
}

# Staging DB Host
output "staging_db_host" {
  value       = try(digitalocean_database_cluster.staging_db[0].host, null)
  description = "Managed DB host for staging"
  sensitive   = false
}

# Production DB Host
output "production_db_host" {
  value       = try(digitalocean_database_cluster.production_db[0].host, null)
  description = "Managed DB host for production"
  sensitive   = false
}
