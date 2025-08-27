variable "do_token" {
  description = "DigitalOcean API token"
  type        = string
  sensitive   = true
}

variable "ssh_key_id" {
  description = "SSH key ID already added in DigitalOcean"
  type        = string
}

variable "region" {
  default     = "fra1"
  description = "DigitalOcean region"
}

variable "image" {
  default     = "docker-20-04"
  description = "Base image (Ubuntu with Docker)"
}

variable "size" {
  default     = "s-1vcpu-1gb"
  description = "Droplet size"
}
