output "staging_ip" {
  value = digitalocean_droplet.staging.ipv4_address
}

output "production_ip" {
  value = digitalocean_droplet.production.ipv4_address
}
